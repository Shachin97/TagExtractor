import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import static java.nio.file.StandardOpenOption.CREATE;

public class TagExtractorFrame extends JFrame {
    JPanel mainPnl;
    JPanel displayPnl;
    JPanel controlPnl;
    JPanel exitPnl;
    JButton selectFileBtn, saveFileBtn, clearBtn, quitBtn;

    JTextArea textArea;
    JScrollPane scrollPane;

    Set<String> stopWords = new HashSet<>();
    Map<String, Integer> map = new HashMap<>();

    public TagExtractorFrame() {


        setSize(650, 700);


        setTitle("Tag Extractor");
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        createGUI();
        setVisible(true);
    }

    private void createGUI() {
        mainPnl = new JPanel();
        displayPnl = new JPanel();
        controlPnl = new JPanel();
        exitPnl = new JPanel();

        mainPnl.add(controlPnl);
        mainPnl.add(displayPnl);
        mainPnl.add(exitPnl);

        add(mainPnl);

        createDisplayPnl();
        createControlPnl();
        exitMenu();
    }

    private void createDisplayPnl() {
        textArea = new JTextArea(30, 50);
        textArea.setFont(new Font("Times New Roman", Font.PLAIN, 15));
        textArea.setEditable(false);

        scrollPane = new JScrollPane(textArea);

        displayPnl.add(scrollPane);


    }

    private void createControlPnl() {

        controlPnl.setLayout(new GridLayout(1, 2));

        selectFileBtn = new JButton("Select file");
        selectFileBtn.addActionListener((ActionEvent ae) -> {
            JFileChooser chooser = new JFileChooser();

            File selectedFile;

            String rec = "";

            ArrayList lines = new ArrayList<>();

            try {
                File workingDirectory = new File(System.getProperty("user.dir"));

                chooser.setCurrentDirectory(workingDirectory);

                if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                    selectedFile = chooser.getSelectedFile();
                    Path file = selectedFile.toPath();

                    InputStream in =
                            new BufferedInputStream(Files.newInputStream(file, CREATE));
                    BufferedReader reader =
                            new BufferedReader(new InputStreamReader(in));

                    textArea.append("Selected File: " +selectedFile.getName() + "\n\n");
                    JOptionPane.showMessageDialog(null, "Chose a Filter File", "Chose File"
                            , JOptionPane.INFORMATION_MESSAGE);
                    FilterFile();
                    Scanner scanner = new Scanner(selectedFile);
                    while (scanner.hasNext()) {
                        lines.add(scanner.next().replace("\t", "").replace(" ", "").
                                replace("-", "").replace("!", "").replace
                                        (".", "").replace(",", "").replace
                                        ("\t\t", "").replace(";", "").replace
                                        ("(", "").replace(")", "").replace
                                        ("'", "").replace("\"", "").
                                replace("'", "").replace("_", "")
                                .replace("[", "").replace("]", "").replace("\\", "")
                                .replace("?", "").replace("\"\"\"", "")
                                .replace(":", "").replace("*", "").replace("{", "")
                                .replace("}", "").replace("", "").replace("'s", "").toLowerCase());
                    }

                    for (int i = 0; i < lines.size(); i++) {
                        if (stopWords.contains((String) lines.get(i))) {
                            lines.remove(i);
                            i--;
                        } else if (((String) lines.get(i)).length() < 3) {
                            lines.remove(i);
                            i--;
                        }
                    }
                    map = (Map<String, Integer>) lines.parallelStream().collect(Collectors.groupingByConcurrent(w -> w, Collectors.counting()));
                    for (Map.Entry<String, Integer> extractWord : map.entrySet()) {
                        textArea.append("Tag:\t" + extractWord.getKey() + "\t\tFrequency:\t\t" + extractWord.getValue() + "\n");
                    }
                    reader.close();
                }
            } catch (FileNotFoundException e) {
                System.out.println("File not found!!!");
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });


        saveFileBtn = new JButton("Save file");
        saveFileBtn.addActionListener((ActionEvent ae) -> {

            String file_name = JOptionPane.showInputDialog(null, "What would you like to save this file as?");

            File workingDirectory = new File(System.getProperty("user.dir"));
            Path file = Paths.get(workingDirectory.getPath() + "\\src\\" + file_name + ".txt");



            try {
                OutputStream out =
                        new BufferedOutputStream(Files.newOutputStream(file, CREATE));
                BufferedWriter writer =
                        new BufferedWriter(new OutputStreamWriter(out));

                writer.write(textArea.getText());
                writer.close();


            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        });

        clearBtn = new JButton("Clear");
        clearBtn.addActionListener((ActionEvent ae) -> textArea.setText(" "));

        quitBtn = new JButton("Quit");
        quitBtn.addActionListener((ActionEvent ae) -> System.exit(0));

        controlPnl.add(selectFileBtn);
        controlPnl.add(saveFileBtn);


    }



    private void FilterFile() {
        JFileChooser chooser = new JFileChooser();

        File selectedFile;

        String rec = "";

        try {
            File workingDirectory = new File(System.getProperty("user.dir"));

            chooser.setCurrentDirectory(workingDirectory);

            if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                selectedFile = chooser.getSelectedFile();
                Path file = selectedFile.toPath();

                InputStream in =
                        new BufferedInputStream(Files.newInputStream(file, CREATE));
                BufferedReader reader =
                        new BufferedReader(new InputStreamReader(in));

                int line = 0;
                while (reader.ready()) {
                    rec = reader.readLine();
                    stopWords.add(rec.toLowerCase());
                    line++;
                }
                reader.close();
            }
        }
        catch (FileNotFoundException e) {
            System.out.println("File not found!!!");
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void exitMenu()

    {

        exitPnl.setLayout(new GridLayout(2,1));
        clearBtn = new JButton("Clear");
        clearBtn.addActionListener((ActionEvent ae) -> textArea.setText(" "));

        quitBtn = new JButton("Quit");
        quitBtn.addActionListener((ActionEvent ae) ->{

        int quit = JOptionPane.showConfirmDialog(null, "Do you want to quit?");
        if (quit == JOptionPane.YES_OPTION) {
            System.exit(0);
        } else
        {

        }

    });

        exitPnl.add(clearBtn);
        exitPnl.add(quitBtn);
    }
}
