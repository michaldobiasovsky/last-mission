package lab;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.TextInputDialog;
import javafx.stage.StageStyle;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Protection {
    private static final String URL = "https://www.fei.vsb.cz/460/cs/kontakt/lide/";

    public static void checkAndApply() {
        TextInputDialog dialog = new TextInputDialog("");
        dialog.initStyle(StageStyle.UTILITY);
        dialog.setTitle("Přihlášení");
        dialog.setHeaderText("Zadejte své přihlašovací jméno.");
        dialog.setContentText("Jméno:");

        Optional<String> result = dialog.showAndWait();
        if (!result.isPresent()) {
            return;
        }
        final String inputName = result.get().trim();
        if (inputName.isEmpty()) return;

        new Thread(() -> {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(new URI(URL).toURL().openStream()))) {
                String fileContent = in.lines().collect(Collectors.joining("\n"));

                Pattern p = Pattern.compile("([A-ZÁČĎÉĚÍŇÓŘŠŤÚŮÝ][a-záčďéěíňóřšťúůý]+(?:\\s+[A-ZÁČĎÉĚÍŇÓŘŠŤÚŮÝ][a-záčďéěíňóřšťúůý]+)+)");
                Matcher m = p.matcher(fileContent);
                Set<String> names = new HashSet<>();
                while (m.find()) {
                    names.add(m.group(1).trim());
                }

                boolean isMember = names.stream().anyMatch(n -> n.equalsIgnoreCase(inputName));

                Platform.runLater(() -> {
                    if (isMember) {
                        System.out.println("Uživatel " + inputName + " je člen Katedry informatiky.");
                        Lemming.speedMultiplier = 0.5;
                        Alert a = new Alert(Alert.AlertType.INFORMATION);
                        a.setTitle("Ověřeno");
                        a.setHeaderText(null);
                        a.setContentText("Bylo zjištěno, že jste člen Katedry informatiky. Lemmingové byli zpomaleni.");
                        a.show();
                    } else {
                        System.out.println("Uživatel " + inputName + " není člen Katedry informatiky.");
                        Lemming.speedMultiplier = 1.0;
                        Alert a = new Alert(Alert.AlertType.INFORMATION);
                        a.setTitle("Neověřeno");
                        a.setHeaderText(null);
                        a.setContentText("Bylo zjištěno, že nejste člen Katedry informatiky.");
                    }
                });

            } catch (IOException | URISyntaxException e) {
                e.printStackTrace();
            }
        }).start();
    }
}
