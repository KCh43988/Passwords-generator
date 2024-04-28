import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Класс, подключающий базу данных
 *
 * @author a1010
 */
class DataBase {
    String url = "jdbc:postgresql://localhost:5432/postgres";
    String DB_username = "postgres";
    String DB_password = "";
    Connection connection;
    Statement st;

    private String readPassword(InputStream inputStream)
            throws IOException {
        StringBuilder resultStringBuilder = new StringBuilder();
        try (BufferedReader br
                     = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = br.readLine()) != null) {
                resultStringBuilder.append(line).append("\n");
            }
        }
        return resultStringBuilder.toString();
    }

    /**
     * Создает соединение с базой данных
     *
     * @throws SQLException возникает если логин или пароль неправильный
     */
    public DataBase() throws SQLException {
        try {
            DB_password = readPassword(new FileInputStream("login_password.txt"));
            this.connection = DriverManager.getConnection(url, DB_username, DB_password);
        }
        catch (IOException e) {
            System.out.println(e.getMessage());
        }
        assert connection != null;
        this.st = connection.createStatement();
    }

    /**
     * Метод, который позволяет сохранять данные о пользователе
     *
     * @param login    логин пользователя
     * @param password пароль пользователя
     * @param link     ссылка, где используется пароль
     */
    public void savePassword(String login, String password, String link) {
        try {
            st.execute("INSERT INTO my_passwords (login, password, link) VALUES ('" + login + "', '" + password + "', '" + link + "')");
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    /**
     * Метод берущий данные о пользователях из базы данных
     *
     * @return возвращает ArrayList из данных пользователей (логин, пароль, ссылка)
     */
    public List<String[]> getPasswords() {
        List<String[]> list = new ArrayList<>();
        try {
            ResultSet resultSet = st.executeQuery("SELECT login, password, link FROM my_passwords;");
            while (resultSet.next()) {
                String[] strings = new String[3];
                strings[0] = resultSet.getString("login");
                strings[1] = resultSet.getString("password");
                strings[2] = resultSet.getString("link");
                list.add(strings);
            }
            return list;
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return new ArrayList<>();
        }
    }
}

/**
 * Класс, который генерирует пароли, состоящие из различных спец. символов, цифр и букв
 *
 * @author a1010
 */
class PasswordGenerator {
    Random random = new Random();
    String password = "";
    char[] chars = {'{', '}', '(', ')', '[', ']', '^', '?', '!', '#', '$', '%', '&', '*'};

    /**
     * Метод, который вызывает метод generateChar length (длинна пароля) раз, тем самым постепенно генерируя пароль
     *
     * @param difficulty сложность пароля (от нее зависит шанс генерации спец. символов и букв)
     * @param length     длинна пароля
     * @return возвращает сгенерированный пароль или "неправильный ввод", при какой либо ошибке
     */
    public String generatePassword(int difficulty, int length) {
        StringBuilder passwordBuilder = new StringBuilder();
        for (int i = 0; i < length; i++) {
            try {
                passwordBuilder.append(generateChar(difficulty));
            } catch (Exception e) {
                return "неправильный ввод";
            }
        }
        password = passwordBuilder.toString();
        return password;
    }

    /**
     * Метод, который принимает сложность пароля, и исходя из этого случайным образом выбирает char (у сложностей разные шансы генерации букв и спец. символов, чем выше сложность, тем выше шанс генерации спец. символа, а с буквами наоборот, чем сложность выше, тем шанс ниже)
     *
     * @param difficulty сложность пароля (от нее зависит шанс генерации спец. символов и букв)
     * @return возвращает char сгенерированный случайным образом или вызывает сам себя при генерации неправильных символов
     * @throws Exception возникает, при генерации неправильных символов
     */
    public char generateChar(int difficulty) throws Exception {
        char result;
        if (difficulty == 1) {
            int rand1 = random.nextInt(1, 67);
            int rand2 = random.nextInt(0, 10);
            int rand3 = random.nextInt(0, 4);

            if (rand1 < 27) {
                // гласные малые
                rand1 += 96;
                result = (char) rand1;
            } else if (rand1 < 53) {
                // гласные заглавные
                rand1 += 38;
                result = (char) rand1;
            } else if (rand1 < 63) {
                // цыфры
                rand2 += 48;
                result = (char) rand2;
            } else {
                // спец знаки
                result = chars[rand3];
            }
        } else if (difficulty == 2) {
            int rand1 = random.nextInt(1, 58);
            int rand2 = random.nextInt(0, 10);
            int rand3 = random.nextInt(0, 9);

            if (rand1 < 20 /*27*/) {
                // гласные малые -7 последних
                rand1 += 96;
                result = (char) rand1;
            } else if (rand1 < 39 /*53*/) {
                // гласные заглавные -7 вначале
                rand1 += 52;
                result = (char) rand1;
            } else if (rand1 < 49 /*63*/) {
                // цыфры
                rand2 += 48;
                result = (char) rand2;
            } else {
                // спец знаки
                result = chars[rand3];
            }
        } else if (difficulty == 3) {
            int rand1 = random.nextInt(1, 51);
            int rand2 = random.nextInt(0, 10);
            int rand3 = random.nextInt(0, 14);

            if (rand1 < 14 /*27*/) {
                // гласные малые -13 последних
                rand1 += 96;
                result = (char) rand1;
            } else if (rand1 < 27 /*53*/) {
                // гласные заглавные -13 вначале
                rand1 += 64;
                result = (char) rand1;
            } else if (rand1 < 37 /*63*/) {
                // цифры
                rand2 += 48;
                result = (char) rand2;
            } else {
                // спец знаки
                result = chars[rand3];
            }
        } else {
            throw new Exception("так нельзя");
        }
        if (result == '\t' || result == '\u0001' || result == '\u0002' || result == '\u0003' || result == '\u0004' || result == '\u0005' || result == '\u0006' || result == '\u0007') {
            return generateChar(difficulty);
        }
        return result;
    }
}

/**
 * Класс, открывающий окно для сохранения пароля, логина и линка в базу данных
 *
 * @author a1010
 */
class PasswordSaverScreen {
    /**
     * Метод, открывающий окно для сохранения пароля, логина и линка в базу данных
     *
     * @param password сгенерированный пароль
     */
    public PasswordSaverScreen(String password) {
        DataBase db;
        try {
            db = new DataBase();
        } catch (SQLException exception) {
            System.err.println(exception.getMessage());
            return;
        }
        JFrame jFrame = new JFrame("Password saver");
        JButton jButton = new JButton("Save");
        JLabel jl1 = new JLabel("Password");
        JLabel jl2 = new JLabel("Login");
        JLabel jl3 = new JLabel("Link");
        JTextField jtf1 = new JTextField(password, 15);
        JTextField jtf2 = new JTextField(15);
        JTextField jtf3 = new JTextField(15);
        JTextField jtf4 = new JTextField(15);
        jFrame.setLayout(new GridLayout(4, 2));
        jFrame.add(jl1);
        jFrame.add(jtf1);
        jFrame.add(jl2);
        jFrame.add(jtf2);
        jFrame.add(jl3);
        jFrame.add(jtf3);
        jFrame.add(jButton);
        jFrame.add(jtf4);
        jButton.addActionListener(e -> {
            db.savePassword(jtf1.getText(), jtf2.getText(), jtf3.getText());
            jtf4.setText("Saved");
            jFrame.repaint();
            jFrame.invalidate();
            jFrame.validate();
        });
        jFrame.setSize(300, 200);
        jFrame.setVisible(true);
    }
}

/**
 * Класс, который открывает окно со всеми сохраненными паролями, логинами и линками
 *
 * @author a1010
 */
class ListSavedPasswords {
    /**
     * Метод, который открывает окно со всеми сохраненными паролями, логинами и линками
     *
     * @param list лист в котором хранятся все пароли, логины и линки
     */
    public ListSavedPasswords(List<String[]> list) {
        JFrame jFrame = new JFrame("Saved password");
        DefaultTableModel tableModel = new DefaultTableModel();
        tableModel.addColumn("Login");
        tableModel.addColumn("Password");
        tableModel.addColumn("Link");
        for (String[] strings : list) {
            tableModel.addRow(strings);
        }
        JTable jTable = new JTable(tableModel);
        ScrollPane scrollPane = new ScrollPane(1);
        scrollPane.add(jTable);
        jFrame.add(scrollPane);
        jFrame.setSize(400, 400);
        jFrame.setVisible(true);
    }
}

/**
 * Класс, который открывает окно главного экрана
 *
 * @author a1010
 */
class MainScreen {

    JFrame frame = new JFrame("Password Generator");

    /**
     * Метод, который открывает окно главного экрана
     *
     * @throws SQLException возникает при неполадках с базой данных
     */
    public MainScreen() throws SQLException {
        PasswordGenerator passwordGenerator = new PasswordGenerator();
        DataBase dataBase = new DataBase();
        JButton jb1 = new JButton("Create password");
        JButton jb2 = new JButton("Save password");
        JTextField jTextField = new JTextField(25);
        DefaultListModel<String> listModel = new DefaultListModel<>();
        JList<String> jList = new JList<>(listModel);
        JButton jb3 = new JButton("Просмотреть пароли");
        listModel.add(0, "Легкий");
        listModel.add(1, "Нормальный");
        listModel.add(2, "Хороший");
        frame.setSize(600, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        frame.setLayout(new FlowLayout());
        frame.add(jb1);
        frame.add(jTextField);
        frame.add(jb2);
        frame.add(jList);
        frame.add(jb3);
        jb1.addActionListener(e -> jTextField.setText(passwordGenerator.generatePassword(jList.getSelectedIndex() + 1, 15)));
        jb2.addActionListener(e -> new PasswordSaverScreen(passwordGenerator.password));
        jb3.addActionListener(e -> new ListSavedPasswords(dataBase.getPasswords()));
    }
}

public class Main {
    public static void main(String[] args) throws SQLException {
        new MainScreen();
    }
}