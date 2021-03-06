package com.example.tcc.doador;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.tcc.Entities.DoacaoBrinquedo;
import com.example.tcc.Entities.DoacaoRoupa;
import com.example.tcc.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.UUID;


public class DoadorNewBrinquedoFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    Button btnEnviarBrinquedo, btnImg1, btnImg2, btnImg3;
    Spinner mSpinnerCondicao, mSpinnerQtd;
    EditText mDescricao, mTipo;
    String spnCondicao, spnQtd;
    ImageView img1, img2, img3;
    Uri mSelectedUri1, mSelectedUri2, mSelectedUri3;
    String uid, tipo, descricao;
    String imagem1, imagem2, imagem3;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.doador_fragment_new_brinquedo, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final NavController navController = Navigation.findNavController(view);

        btnEnviarBrinquedo = view.findViewById(R.id.btnEnviarBrinquedo);
        btnImg1 = view.findViewById(R.id.btnAddImagemBrinquedo1);
        btnImg2 = view.findViewById(R.id.btnAddImagemBrinquedo2);
        btnImg3 = view.findViewById(R.id.btnAddImagemBrinquedo3);
        mTipo = view.findViewById(R.id.edtTipoBrinquedo);
        mSpinnerCondicao = view.findViewById(R.id.spnCondicaoBrinquedo);
        mSpinnerQtd = view.findViewById(R.id.spnQuantidadeBrinquedo);
        mDescricao = view.findViewById(R.id.edtDescricaoBrinquedo);
        img1 = view.findViewById(R.id.imgAddImagemBrinquedo1);
        img2 = view.findViewById(R.id.imgAddImagemBrinquedo2);
        img3 = view.findViewById(R.id.imgAddImagemBrinquedo3);

        ArrayAdapter<CharSequence> adapterCondicao = ArrayAdapter.createFromResource(view.getContext(), R.array.spnCondicaoDoacao, android.R.layout.simple_spinner_item);
        adapterCondicao.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        ArrayAdapter<CharSequence> adapterQtd = ArrayAdapter.createFromResource(view.getContext(), R.array.spnQuantidadeDoacao, android.R.layout.simple_spinner_item);
        adapterQtd.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        mSpinnerCondicao.setAdapter(adapterCondicao);
        mSpinnerCondicao.setOnItemSelectedListener(this);
        mSpinnerQtd.setAdapter(adapterQtd);
        mSpinnerQtd.setOnItemSelectedListener(this);

        btnImg1.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectPhoto1();
            }
        }));

        btnImg2.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectPhoto2();
            }
        }));

        btnImg3.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectPhoto3();
            }
        }));

        btnEnviarBrinquedo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DoarRoupa();
//                navController.navigate(R.id.action_newRoupaFragment_to_doacaoConcluidaFragment);
            }


            private void DoarRoupa() {

                uid = FirebaseAuth.getInstance().getUid();
                tipo = mTipo.getText().toString();
                descricao = mDescricao.getText().toString();
                Log.i("roupa", "tamanho");
                Log.i("roupa", "descricao");

                if (tipo.isEmpty() ||  descricao.isEmpty() || spnCondicao.equals("Escolha...") || spnQtd.equals("Escolha...") || (mSelectedUri1 == null)){
                    Toast.makeText(view.getContext(), "Preencha tudo parça. TA LOKÃO?", Toast.LENGTH_SHORT).show();
                    Log.i("roupa", "não foi");
                    return;
                }

                saveDoacaoInFirebase();

            }
        });
    }

    private void saveDoacaoInFirebase() {

        uid = FirebaseAuth.getInstance().getUid();
        String foto1 = UUID.randomUUID().toString();
        final StorageReference ref1 = FirebaseStorage.getInstance().getReference("/DoacaoBrinquedo/" + uid + foto1);
        ref1.putFile(mSelectedUri1).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                ref1.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        imagem1 = uri.toString();
                        Log.i("sucesso", imagem1);
                    }
                });
            }
        });

        if (mSelectedUri2 != null){
            String foto2 = UUID.randomUUID().toString();
            final StorageReference ref2 = FirebaseStorage.getInstance().getReference("/DoacaoBrinquedo/" + uid + foto2);
            ref2.putFile(mSelectedUri2).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    ref2.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            imagem2 = uri.toString();
                        }
                    });
                }
            });
        }

        if (mSelectedUri3 != null){
            String foto3 = UUID.randomUUID().toString();
            final StorageReference ref3 = FirebaseStorage.getInstance().getReference("/DoacaoBrinquedo/" + uid + foto3);
            ref3.putFile(mSelectedUri3).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    ref3.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            imagem3 = uri.toString();
                        }
                    });
                }
            });
        }

        final String iddoacao = UUID.randomUUID().toString();
        final String status = "Aguardando";
        final String unica_ou_camp = "unica";
        final String categoria = "Brinquedo";

        new CountDownTimer(20000, 1000) {
            @Override
            public void onTick(long l) {

            }

            @Override
            public void onFinish() {


                DoacaoBrinquedo novo_Brinquedo = new DoacaoBrinquedo(iddoacao, uid, null, tipo, spnQtd, spnCondicao, descricao,
                        imagem1, imagem2, imagem3, status, unica_ou_camp, categoria);

                FirebaseFirestore.getInstance().collection("AguardandoOng")
                        .document(iddoacao)
                        .set(novo_Brinquedo)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.i("sucesso", "Processo finalizado");
                            }
                        });

            }
        }.start();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 6) {
            mSelectedUri1 = data.getData();
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), mSelectedUri1);
                img1.setImageDrawable(new BitmapDrawable(bitmap));
                btnImg1.setAlpha(0);
            } catch (IOException e) { }
        }

        if (requestCode == 7) {
            mSelectedUri2 = data.getData();
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), mSelectedUri2);
                img2.setImageDrawable(new BitmapDrawable(bitmap));
                btnImg2.setAlpha(0);
            } catch (IOException e) { }
        }

        if (requestCode == 8) {
            mSelectedUri3 = data.getData();
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), mSelectedUri3);
                img3.setImageDrawable(new BitmapDrawable(bitmap));
                btnImg3.setAlpha(0);
            } catch (IOException e) { }
        }
    }

    private void selectPhoto1() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, 6);
    }

    private void selectPhoto2() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, 7);
    }

    private void selectPhoto3() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, 8);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        String spn = adapterView.getItemAtPosition(i).toString();
        spnCondicao = mSpinnerCondicao.getSelectedItem().toString();
        spnQtd = mSpinnerQtd.getSelectedItem().toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}

