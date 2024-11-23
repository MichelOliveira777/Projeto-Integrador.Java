import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class ProjetoPI3 {

    // Classe de conexão com o banco de dados
    public static class Conexao {
        private String url = "jdbc:mysql://localhost:3306/estoque";
        private String user = "root";
        private String password = "michel2003";

        public Connection getConnection() throws SQLException {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver"); // Carrega o driver
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                throw new SQLException("Driver JDBC não encontrado.");
            }
            return DriverManager.getConnection(url, user, password);
        }
    }

    // Classe de cadastro e login
    public static class Cadastro {
        private Conexao conexao = new Conexao();

        public boolean cadastrarUsuario(String nome, String login, String senha, String email, String telefone,
                String cpf, boolean isAdm) {
            String sql = "INSERT INTO Usuario (Nome, Login, Senha, Email, Telefone, CPF, ADM) VALUES (?, ?, ?, ?, ?, ?, ?)";
            try (Connection conn = conexao.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, nome);
                stmt.setString(2, login);
                stmt.setString(3, senha);
                stmt.setString(4, email);
                stmt.setString(5, telefone);
                stmt.setString(6, cpf);
                stmt.setBoolean(7, isAdm);
                stmt.executeUpdate();
                return true;
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        }

        public boolean loginUsuario(String login, String senha) {
            String sql = "SELECT * FROM Usuario WHERE Login = ? AND Senha = ?";
            try (Connection conn = conexao.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, login);
                stmt.setString(2, senha);
                ResultSet rs = stmt.executeQuery();
                return rs.next();
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        }
    }

    private static Cadastro cadastro = new Cadastro();
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        while (true) {
            System.out.println("Escolha uma opção:");
            System.out.println("1. Cadastrar");
            System.out.println("2. Login");
            System.out.println("0. Sair");

            int opcao = scanner.nextInt();
            scanner.nextLine(); // Limpa a entrada

            switch (opcao) {
                case 1:
                    cadastrarUsuario();
                    break;
                case 2:
                    loginUsuario();
                    break;
                case 0:
                    System.out.println("Saindo...");
                    scanner.close();
                    return;
                default:
                    System.out.println("Opção inválida!");
            }
        }
    }

    private static void cadastrarUsuario() {
        System.out.println("Cadastro de Novo Usuário");

        System.out.print("Nome: ");
        String nome = scanner.nextLine();

        System.out.print("Login: ");
        String login = scanner.nextLine();

        System.out.print("Senha: ");
        String senha = scanner.nextLine();

        System.out.print("Email: ");
        String email = scanner.nextLine();

        System.out.print("Telefone: ");
        String telefone = scanner.nextLine();

        System.out.print("CPF: ");
        String cpf = scanner.nextLine();

        System.out.print("É administrador? (1 para sim, 0 para não): ");
        boolean isAdm = scanner.nextInt() == 1;
        scanner.nextLine(); // Limpa a entrada

        boolean sucesso = cadastro.cadastrarUsuario(nome, login, senha, email, telefone, cpf, isAdm);
        if (sucesso) {
            System.out.println("Cadastro realizado com sucesso!");
        } else {
            System.out.println("Erro ao realizar cadastro.");
        }
    }

    private static void loginUsuario() {
        System.out.println("Login de Usuário");

        System.out.print("Login: ");
        String login = scanner.nextLine();

        System.out.print("Senha: ");
        String senha = scanner.nextLine();

        boolean sucesso = cadastro.loginUsuario(login, senha);
        if (sucesso) {
            System.out.println("Login realizado com sucesso!");
        } else {
            System.out.println("Login ou senha incorretos.");
        }
    }
}
