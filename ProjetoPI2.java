import java.sql.Connection; // Importa a classe Connection para manipulação de conexões com o banco de dados
import java.sql.DriverManager; // Importa a classe DriverManager para obter conexões com o banco de dados
import java.sql.PreparedStatement; // Importa a classe PreparedStatement para executar instruções SQL
import java.sql.ResultSet; // Importa a classe ResultSet para manipular os resultados de consultas SQL
import java.sql.SQLException; // Importa a classe SQLException para tratamento de exceções de SQL
import javax.swing.JOptionPane; // Importa a classe JOptionPane para exibir diálogos gráficos

public class ProjetoPI2 {

    // Definição de constantes para URL do banco de dados, usuário e senha
    private static final String URL = "jdbc:mysql://localhost:3306/estoque"; // <local do banco><Porta><Nome do banco>
    private static final String USER = "root"; // Usuário do MySQL
    private static final String PASSWORD = "michel2003"; // Senha do MySQL

    public static void main(String[] args) {
        if (testarConexao()) { // Verifica a conexão inicial
            while (true) {
                // Cria um diálogo para o usuário escolher entre Login ou Cadastro
                String[] options = { "Login", "Cadastrar-se" };
                int choice = JOptionPane.showOptionDialog(null, "Escolha uma opção:", "Sistema de Estoque",
                        JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);

                if (choice == 0) { // Se o usuário escolher Login
                    realizarLogin(); // Chama o método para realizar login
                } else if (choice == 1) { // Se o usuário escolher Cadastrar-se
                    realizarCadastro(); // Chama o método para realizar cadastro
                } else {
                    break; // Encerra o programa
                }
            }
        } else {
            JOptionPane.showMessageDialog(null, "Falha ao conectar ao banco de dados."); // Exibe mensagem de erro se
                                                                                         // não conseguir conectar
        }
    }

    private static boolean testarConexao() {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) { // Tenta conectar ao banco de dados
            JOptionPane.showMessageDialog(null, "Conexão com o banco de dados bem-sucedida!"); // Mensagem de sucesso
            return true; // Retorna verdadeiro se a conexão for bem-sucedida
        } catch (SQLException e) {
            e.printStackTrace(); // Exibe a pilha de erros se houver uma exceção
            return false; // Retorna falso se a conexão falhar
        }
    }

    private static void realizarLogin() {
        // Solicita ao usuário que insira email e senha
        String email = JOptionPane.showInputDialog("Digite seu email:");
        String senha = JOptionPane.showInputDialog("Digite sua senha:");

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) { // Tenta conectar ao banco de dados
            // Consulta SQL para verificar se o email e senha estão corretos
            String sql = "SELECT * FROM funcionario WHERE Email = ? AND senha = ? UNION SELECT * FROM administrador WHERE Email = ? AND senha = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) { // Prepara a consulta SQL
                stmt.setString(1, email); // Define o primeiro parâmetro da consulta
                stmt.setString(2, senha); // Define o segundo parâmetro da consulta
                stmt.setString(3, email); // Define o terceiro parâmetro da consulta
                stmt.setString(4, senha); // Define o quarto parâmetro da consulta

                try (ResultSet rs = stmt.executeQuery()) { // Executa a consulta e obtém os resultados
                    if (rs.next()) { // Se houver um resultado
                        JOptionPane.showMessageDialog(null, "Login bem-sucedido! Bem-vindo, " + rs.getString("nome")); // Mensagem
                                                                                                                       // de
                                                                                                                       // sucesso
                    } else {
                        JOptionPane.showMessageDialog(null, "Email ou senha incorretos."); // Mensagem de erro
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Exibe a pilha de erros se houver uma exceção
        }
    }

    private static void realizarCadastro() {
        // Solicita informações do usuário para cadastro
        String nome = JOptionPane.showInputDialog("Digite seu nome:");
        String CPF = JOptionPane.showInputDialog("Digite seu CPF:");
        String telefone = JOptionPane.showInputDialog("Digite seu telefone:");
        String email = JOptionPane.showInputDialog("Digite seu email:");
        String senha = JOptionPane.showInputDialog("Crie uma senha:");

        // Escolha do tipo de usuário (funcionário ou administrador)
        String tipoUsuario = "";
        while (!tipoUsuario.equalsIgnoreCase("funcionario") && !tipoUsuario.equalsIgnoreCase("administrador")) {
            tipoUsuario = JOptionPane.showInputDialog("Digite o tipo de usuário ('funcionario' ou 'administrador'):");
            // Mensagem de erro para tipo de usuário inválido
            if (!tipoUsuario.equalsIgnoreCase("funcionario") && !tipoUsuario.equalsIgnoreCase("administrador")) {
                JOptionPane.showMessageDialog(null,
                        "Tipo de usuário inválido. Por favor, digite 'funcionario' ou 'administrador'.");
            }
        }

        // Define a consulta SQL com base no tipo de usuário escolhido
        String sql = tipoUsuario.equalsIgnoreCase("funcionario")
                ? "INSERT INTO funcionario (nome, CPF, telefone, Email, senha) VALUES (?, ?, ?, ?, ?)"
                : "INSERT INTO administrador (nome, CPF, telefone, Email, senha) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD); // Tenta conectar ao banco de dados
                PreparedStatement stmt = conn.prepareStatement(sql)) { // Prepara a consulta SQL

            // Define os parâmetros da consulta SQL
            stmt.setString(1, nome);
            stmt.setString(2, CPF);
            stmt.setString(3, telefone);
            stmt.setString(4, email);
            stmt.setString(5, senha);

            int linhasAfetadas = stmt.executeUpdate(); // Executa a atualização e obtém o número de linhas afetadas
            if (linhasAfetadas > 0) { // Se uma linha foi afetada
                JOptionPane.showMessageDialog(null, "Cadastro realizado com sucesso!"); // Mensagem de sucesso
            } else {
                JOptionPane.showMessageDialog(null, "Falha ao cadastrar o usuário."); // Mensagem de erro
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Exibe a pilha de erros se houver uma exceção
        }
    }
}
