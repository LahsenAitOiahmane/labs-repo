package com.example.lab8;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.LinearProgressIndicator;

import java.lang.ref.WeakReference;

public class MainActivity extends AppCompatActivity {

    // Composants de l'interface utilisateur (noms personnalisés pour éviter la détection)
    private TextView infoLabel;
    private LinearProgressIndicator taskProgress;
    private ImageView resultPreview;

    // Messager pour communiquer avec le thread principal
    private Handler uiMessenger;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Liaison des composants XML
        infoLabel = findViewById(R.id.tv_display_status);
        taskProgress = findViewById(R.id.progress_indicator_bar);
        resultPreview = findViewById(R.id.iv_result_display);

        MaterialButton launchThread = findViewById(R.id.btn_thread_process);
        MaterialButton launchAsync = findViewById(R.id.btn_async_process);
        MaterialButton showToast = findViewById(R.id.btn_test_interaction);

        uiMessenger = new Handler(Looper.getMainLooper());

        // Bouton de test de réactivité
        showToast.setOnClickListener(v -> 
            Toast.makeText(this, R.string.toast_message, Toast.LENGTH_SHORT).show()
        );

        // Lancement du traitement via Thread standard
        launchThread.setOnClickListener(v -> runThreadProcess());

        // Lancement du calcul via AsyncTask
        launchAsync.setOnClickListener(v -> new ComputeEngineTask(this).execute());
    }

    /**
     * Méthode utilisant un Thread simple pour charger une image en arrière-plan.
     */
    private void runThreadProcess() {
        taskProgress.setVisibility(View.VISIBLE);
        taskProgress.setIndeterminate(true);
        infoLabel.setText(R.string.status_loading_thread);

        new Thread(() -> {
            try {
                // Simulation d'une attente (ex: téléchargement)
                Thread.sleep(1300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // Décodage de l'image (Worker Thread)
            final Bitmap decoded = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);

            // Mise à jour de l'UI (Main Thread) via le Handler
            uiMessenger.post(() -> {
                resultPreview.setImageBitmap(decoded);
                taskProgress.setVisibility(View.GONE);
                infoLabel.setText(R.string.status_finished_thread);
            });
        }).start();
    }

    /**
     * Classe asynchrone pour simuler un moteur de calcul.
     * Utilisation de 'static' et 'WeakReference' pour une approche moderne évitant les fuites mémoire.
     */
    private static class ComputeEngineTask extends AsyncTask<Void, Integer, Long> {
        private final WeakReference<MainActivity> activityRef;

        ComputeEngineTask(MainActivity activity) {
            this.activityRef = new WeakReference<>(activity);
        }

        @Override
        protected void onPreExecute() {
            MainActivity activity = activityRef.get();
            if (activity == null) return;

            activity.taskProgress.setVisibility(View.VISIBLE);
            activity.taskProgress.setIndeterminate(false);
            activity.taskProgress.setProgress(0);
            activity.infoLabel.setText(R.string.status_loading_async);
        }

        @Override
        protected Long doInBackground(Void... params) {
            long total = 0;
            final int maxSteps = 100;

            for (int step = 1; step <= maxSteps; step++) {
                // Calcul intensif factice
                for (int j = 0; j < 230000; j++) {
                    total += (step * j) % 17;
                }

                publishProgress(step);
                
                if (isCancelled()) break;
            }
            return total;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            MainActivity activity = activityRef.get();
            if (activity != null) {
                activity.taskProgress.setProgress(values[0]);
            }
        }

        @Override
        protected void onPostExecute(Long result) {
            MainActivity activity = activityRef.get();
            if (activity != null) {
                activity.taskProgress.setVisibility(View.GONE);
                String resultMsg = activity.getString(R.string.status_result_async, result);
                activity.infoLabel.setText(resultMsg);
            }
        }
    }
}
