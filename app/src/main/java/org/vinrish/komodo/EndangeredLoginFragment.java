package org.vinrish.komodo;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.vinrish.komodo.databinding.FragmentLoginBinding;

public class EndangeredLoginFragment extends Fragment {
    private static final String TAG = "LoginFragment";
    private FirebaseAuth auth;
    private FragmentLoginBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentLoginBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        auth = FirebaseAuth.getInstance();

        binding.btnLogin.setOnClickListener(v -> loginUser());

        binding.tvRegister.setOnClickListener(v ->
                NavHostFragment.findNavController(EndangeredLoginFragment.this)
                        .navigate(R.id.action_loginFragment_to_registerFragment)
        );
    }

    private void loginUser() {
        String email = binding.etEmail.getText().toString().trim();
        String password = binding.etPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            Snackbar.make(requireView(), "Please enter an email.", Snackbar.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            Snackbar.make(requireView(), "Please enter a password.", Snackbar.LENGTH_SHORT).show();
            return;
        }

        // Sign in with Firebase
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(getActivity(), task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = auth.getCurrentUser();
                        Snackbar.make(requireView(), "Login successful.", Snackbar.LENGTH_SHORT).show();
                        updateUI(user);
                    } else {
                        Snackbar.make(requireView(), "Authentication failed.", Snackbar.LENGTH_SHORT).show();
                        updateUI(null);
                    }
                });
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).setLoggedIn(true);
            }
            Snackbar.make(requireView(), "Welcome " + user.getEmail(), Snackbar.LENGTH_LONG).show();

            NavHostFragment.findNavController(EndangeredLoginFragment.this)
                    .navigate(R.id.action_loginFragment_to_animalFragment);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}