/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package src.br.edu.ifms.lp2.ui;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.FileChooser;
import javafx.util.Duration;

/**
 *
 * @author sidne
 */
public class PrincipalFXMLController implements Initializable {

    @FXML
    private Label labelMusica;

    @FXML
    private ImageView imagemMusica;

    @FXML
    private Button aleatorio;

    @FXML
    private Button stop;

    @FXML
    private Button next;

    @FXML
    private Button prev;

    @FXML
    private Button repeat;

    @FXML
    private Label tempoFinal;

    @FXML
    private Label tempo;

    @FXML
    private Button botaoPlay;

    @FXML
    private Button botaoAbrir;

    @FXML
    private Button botaoPause;

    @FXML
    private ProgressBar barraProgresso;

    private List<File> arquivosMusicas = new ArrayList<>();

    private MediaPlayer player = null;

    private MediaPlayer proxPlayer = null;

    private List<MediaPlayer> players = null;

    private final MediaView mediaView = new MediaView();

    private int indice = 0;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        botaoAbrir.setOnAction((ActionEvent e) -> {
            FileChooser janelaArquivo = new FileChooser();
            janelaArquivo.setTitle("Escolha uma música (.mp3)");
            janelaArquivo.getExtensionFilters().add(new FileChooser.ExtensionFilter("MP3", "*.mp3"));
            arquivosMusicas = janelaArquivo.showOpenMultipleDialog(labelMusica.getScene().getWindow());
            criarPlayers(arquivosMusicas);
        });

        repeat.setOnAction((event) -> {
            if (indice > 0) {
                indice--;
            }
//            if (mediaView.getMediaPlayer() != null) {
//                mediaView.getMediaPlayer().stop();
//                mediaView.getMediaPlayer().play();
//            }
        });

        botaoPlay.setOnAction((ActionEvent e) -> {
            mediaView.setMediaPlayer(players.get(indice));
            mediaView.getMediaPlayer().setOnEndOfMedia(() -> {
                mediaView.getMediaPlayer().stop();
                proxima();
            });
            getInfo();
            getTempos(mediaView.getMediaPlayer());
            if (mediaView.getMediaPlayer() != null) {
                mediaView.getMediaPlayer().play();
            }
        });

        botaoPause.setOnAction((ActionEvent e) -> {
            if (mediaView.getMediaPlayer() != null) {
                mediaView.getMediaPlayer().pause();
            }
        });

        next.setOnAction((event) -> {
            if (mediaView.getMediaPlayer() != null) {
                mediaView.getMediaPlayer().stop();
                proxima();
                getInfo();
                getTempos(mediaView.getMediaPlayer());
            }
        });

        prev.setOnAction((event) -> {
            mediaView.getMediaPlayer().stop();
            zeraValores();
            if (indice > 0) {
                indice--;
            } else {
                indice = 0;
            }
            mediaView.setMediaPlayer(players.get(indice));
            mediaView.getMediaPlayer().play();
        });

        stop.setOnAction((event) -> {
            mediaView.getMediaPlayer().stop();
            indice = 0;
        });

        aleatorio.setOnAction((event) -> {
            Collections.shuffle(players);
        });

    }

    public void getInfo() {
        Object item = mediaView.getMediaPlayer().getMedia().getMetadata().get("image");
        if (item != null) {
            imagemMusica.setVisible(true);
            imagemMusica.setImage((Image) item);
        }
        Object objTitulo = mediaView.getMediaPlayer().getMedia().getMetadata().get("title");
        Object objArtista = mediaView.getMediaPlayer().getMedia().getMetadata().get("album artist");
        String texto = "";
        String dados = mediaView.getMediaPlayer().getMedia().getMetadata().toString();
        if (objTitulo != null) {
            texto += (String) objTitulo;
        } else {
            texto += "Nova música";
        }
        if (objArtista != null) {
            texto += " - " + objArtista;
        }
        labelMusica.setText(texto);
    }

    public void getTempos(MediaPlayer player) {
        mediaView.getMediaPlayer().currentTimeProperty().addListener((ObservableValue<? extends Duration> observable, Duration oldValue, Duration newValue) -> {
            Duration duracao = mediaView.getMediaPlayer().getMedia().getDuration();
            double duracaoEmSegundosDaFaixa = duracao.toSeconds();
            double tempoAtualSegundos = oldValue.toSeconds();
            double progresso = tempoAtualSegundos / duracaoEmSegundosDaFaixa;
            barraProgresso.setProgress(progresso);
            System.out.println(progresso);
            String tempoFinalFormatado = String.format("%.2f", duracao.toMinutes());
            String tempoAtualFormatado = String.format("%.2f", oldValue.toMinutes());
            tempo.setText(tempoAtualFormatado.replace(",", ":"));
            tempoFinal.setText(tempoFinalFormatado.replace(",", ":"));
            if (progresso > 0.99) {
                mediaView.getMediaPlayer().stop();
                barraProgresso.setProgress(0);

            }
        });
    }

    private void criarPlayers(List<File> musicas) {
        if (players == null) {
            players = new ArrayList<>();
        }
        if (musicas != null) {
            for (File musica : musicas) {
                Media media = new Media(musica.toURI().toString());
                MediaPlayer player = new MediaPlayer(media);
                players.add(player);
            }
        }
    }

    public boolean proxima() {
        zeraValores();
        if (indice < players.size() - 1) {
            indice++;
            System.out.println(indice);
            mediaView.setMediaPlayer(players.get(indice));
            mediaView.getMediaPlayer().play();
            return true;
        } else {
            indice = 0;
            mediaView.setMediaPlayer(players.get(indice));
            mediaView.getMediaPlayer().play();
        }
        return false;
    }

    public void zeraValores() {
        barraProgresso.setProgress(0);
        tempo.setText("0:00");
        tempoFinal.setText("0:00");
    }
}
