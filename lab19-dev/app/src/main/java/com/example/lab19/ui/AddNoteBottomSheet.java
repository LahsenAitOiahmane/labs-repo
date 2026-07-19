package com.example.lab19.ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.lab19.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

/**
 * AddNoteBottomSheet — dialogue glissant depuis le bas pour saisir une nouvelle note.
 *
 * Ce fragment affiche un formulaire (titre + contenu) et notifie
 * l'Activity via une interface callback lorsque l'utilisateur valide.
 */
public class AddNoteBottomSheet extends BottomSheetDialogFragment {

    public static final String TAG_SHEET = "AddNoteBottomSheet";

    private TextInputLayout tilTitle;
    private TextInputLayout tilBody;
    private TextInputEditText etTitle;
    private TextInputEditText etBody;

    /** Callback pour transmettre les données saisies à l'Activity */
    public interface OnNoteSubmitListener {
        void onNoteSubmit(String title, String content);
    }

    private OnNoteSubmitListener submitListener;

    public void setOnNoteSubmitListener(OnNoteSubmitListener listener) {
        this.submitListener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_sheet_add_note, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tilTitle = view.findViewById(R.id.til_title);
        tilBody = view.findViewById(R.id.til_body);
        etTitle = view.findViewById(R.id.et_note_title);
        etBody = view.findViewById(R.id.et_note_body);

        MaterialButton btnSave = view.findViewById(R.id.btn_sheet_save);
        MaterialButton btnCancel = view.findViewById(R.id.btn_sheet_cancel);

        btnSave.setOnClickListener(v -> handleSave());
        btnCancel.setOnClickListener(v -> dismiss());
    }

    /**
     * Valide les champs, puis notifie le listener ou affiche une erreur.
     */
    private void handleSave() {
        String rawTitle = etTitle.getText() != null ? etTitle.getText().toString().trim() : "";
        String rawBody = etBody.getText() != null ? etBody.getText().toString().trim() : "";

        boolean isValid = true;

        if (TextUtils.isEmpty(rawTitle)) {
            tilTitle.setError("Le titre est obligatoire");
            isValid = false;
        } else {
            tilTitle.setError(null);
        }

        if (TextUtils.isEmpty(rawBody)) {
            tilBody.setError("Le contenu est obligatoire");
            isValid = false;
        } else {
            tilBody.setError(null);
        }

        if (isValid && submitListener != null) {
            submitListener.onNoteSubmit(rawTitle, rawBody);
            dismiss();
        }
    }

    @Override
    public int getTheme() {
        return com.google.android.material.R.style.ThemeOverlay_Material3_BottomSheetDialog;
    }
}
