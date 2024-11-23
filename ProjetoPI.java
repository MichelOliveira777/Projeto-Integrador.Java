
/*PROJETO INTEGRADOR: SISTEMA DE ESTOQUE, COMPRAS E VENDAS DE CAMISETAS DE TIMES
 *CRIADOR: MICHEL OLIVEIRA CORREIA DA SILVA 
 *DATA DE CRIAÇÃO: 23/09/2024
 *DATA DE FINALIZAÇÃO: 23/11/2024
 */
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.InputMismatchException;
import java.util.Scanner;

public class ProjetoPI {

    private static final String URL = "jdbc:mysql://localhost:3306/estoque";
    private static final String USER = "root"; // Seu usuário do MySQL da maquina
    private static final String PASSWORD = "michel2003"; // Sua senha do MySQL da maquina

    public static void main(String[] args) {
        try (Connection conn = conectar()) {
            if (conn != null) {
                System.out.println("Conexão com o banco de dados estabelecida.");
                while (true) {
                    realizarLogin(conn);
                }
            } else {
                System.out.println("Falha ao conectar ao banco de dados.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Método para conectar ao banco de dados
    private static Connection conectar() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver"); // Carregar o driver MySQL
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException e) {
            System.out.println("Driver do MySQL não encontrado.");
            return null;
        }
    }

    // Método para realizar o login
    private static void realizarLogin(Connection conn) {
        Scanner sc = new Scanner(System.in);

        System.out.println("=====DevSports=====");
        System.out.print("Digite seu login: ");
        String login = sc.nextLine();

        System.out.print("Digite sua senha: ");
        String senha = sc.nextLine();

        String sql = "SELECT * FROM Usuario WHERE Login = ? AND Senha = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, login);
            stmt.setString(2, senha);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                boolean isAdmin = rs.getBoolean("ADM");
                String nomeUsuario = rs.getString("Nome");
                System.out.println("Login bem-sucedido! Bem-vindo à DevSports, " + nomeUsuario);

                if (isAdmin) {
                    exibirMenuAdministrador(conn);
                } else {
                    exibirMenuFuncionario(conn);
                }
            } else {
                System.out.println("Login ou senha incorretos. Tente novamente.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Método para exibir o menu do Administrador
    private static void exibirMenuAdministrador(Connection conn) {
        Scanner sc = new Scanner(System.in);
        int opcao;

        do {
            System.out.println("Menu do Administrador:");
            System.out.println("1. Consultar Estoque");
            System.out.println("2. Cadastrar Camisetas");
            System.out.println("3. Gerenciar Estoque");
            System.out.println("4. Gerenciar Usuários");
            System.out.println("5. Sair");

            while (!sc.hasNextInt()) {
                System.out.println("Opção inválida.");
                sc.next(); // Limpa a entrada inválida
            }

            opcao = sc.nextInt();
            sc.nextLine();

            switch (opcao) {
                case 1:
                    consultarEstoque(conn, sc);
                    break;
                case 2:
                    cadastrarCamisetas(conn, sc);
                    break;
                case 3:
                    gerenciarEstoque(conn, sc);
                    break;
                case 4:
                    gerenciarUsuarios(conn, sc);
                    break;
                case 5:
                    System.out.println("Saindo...");
                    break;
                default:
                    System.out.println("Opção inválida.");
            }
        } while (opcao != 5);
    }

    // Método para exibir o menu do Funcionário
    private static void exibirMenuFuncionario(Connection conn) {
        Scanner sc = new Scanner(System.in);
        int opcao;

        do {
            System.out.println("Menu do Funcionário:");
            System.out.println("1. Consultar Estoque");
            System.out.println("2. Cadastrar Camisetas");
            System.out.println("3. Gerenciar Estoque");
            System.out.println("4. Sair");

            while (!sc.hasNextInt()) {
                System.out.println("Opção inválida. Por favor, insira um número.");
                sc.next(); // Limpa a entrada inválida
            }

            opcao = sc.nextInt();
            sc.nextLine();

            switch (opcao) {
                case 1:
                    consultarEstoque(conn, sc);
                    break;
                case 2:
                    cadastrarCamisetas(conn, sc);
                    break;
                case 3:
                    gerenciarEstoque(conn, sc);
                    break;
                case 4:
                    System.out.println("Saindo...");
                    break;
                default:
                    System.out.println("Opção inválida.");
            }
        } while (opcao != 4);
    }

    // Método para consultar estoque
    private static void consultarEstoque(Connection conn, Scanner sc) {
        String opcao = "";

        // Validação da entrada para aceitar apenas "filtrar" ou "visualizar"
        while (true) {
            System.out.println("Deseja filtrar ou visualizar todo o estoque? (filtrar/visualizar)");
            opcao = sc.nextLine().trim().toLowerCase();

            if (opcao.equals("filtrar") || opcao.equals("visualizar")) {
                break;
            } else {
                System.out.println("Entrada inválida. Por favor, digite 'filtrar' ou 'visualizar'.");
            }
        }

        String sql = "SELECT * FROM Produto";
        boolean aplicarFiltros = false;

        // Variáveis para os filtros
        String codigo = null;
        String liga = null;
        String tamanho = null;
        String clube = null;
        int ano = 0;

        if (opcao.equals("filtrar")) {
            aplicarFiltros = true;

            // Filtro por Clube
            System.out.print("Informe o nome do clube: ");
            clube = sc.nextLine().trim();

            // Pergunta para cada filtro adicional
            System.out.print("Deseja filtrar pelo código da camiseta? (s/n): ");
            if (sc.nextLine().trim().equalsIgnoreCase("s")) {
                System.out.print("Informe o código da camiseta: ");
                codigo = sc.nextLine().trim();
            }

            // Liga
            System.out.print("Deseja filtrar pela liga? (s/n): ");
            if (sc.nextLine().trim().equalsIgnoreCase("s")) {
                while (true) {
                    System.out.println("Selecione a liga:");
                    System.out.println("1 - Brasileirão");
                    System.out.println("2 - Premier League");
                    System.out.println("3 - Serie A");
                    System.out.println("4 - Bundesliga");
                    System.out.println("5 - La Liga");
                    System.out.println("6 - Ligue 1");
                    System.out.println("7 - Eredivisie");
                    System.out.println("8 - Liga Portuguesa");
                    System.out.println("9 - Liga Saudita");
                    System.out.println("10 - MLS");
                    System.out.println("11 - Seleção");
                    if (sc.hasNextInt()) {
                        int ligaOpcao = sc.nextInt();
                        sc.nextLine(); // Limpa o buffer
                        switch (ligaOpcao) {
                            case 1 -> liga = "Brasileirão";
                            case 2 -> liga = "Premier League";
                            case 3 -> liga = "Serie A";
                            case 4 -> liga = "Bundesliga";
                            case 5 -> liga = "La Liga";
                            case 6 -> liga = "Ligue 1";
                            case 7 -> liga = "Eredivisie";
                            case 8 -> liga = "Liga Portuguesa";
                            case 9 -> liga = "Liga Saudita";
                            case 10 -> liga = "MLS";
                            case 11 -> liga = "Seleção";
                            default -> {
                                System.out.println("Opção inválida. Tente novamente.");
                                continue;
                            }
                        }
                        break;
                    } else {
                        System.out.println("Entrada inválida. Por favor, informe um número válido.");
                        sc.nextLine(); // Limpa o buffer
                    }
                }
            }

            // Tamanho
            System.out.print("Deseja filtrar pelo tamanho? (s/n): ");
            if (sc.nextLine().trim().equalsIgnoreCase("s")) {
                while (true) {
                    System.out.println("Informe o tamanho:");
                    System.out.println("1 - PP");
                    System.out.println("2 - P");
                    System.out.println("3 - M");
                    System.out.println("4 - G");
                    System.out.println("5 - GG");
                    System.out.println("6 - XG");
                    if (sc.hasNextInt()) {
                        int tamanhoOpcao = sc.nextInt();
                        sc.nextLine(); // Limpa o buffer
                        switch (tamanhoOpcao) {
                            case 1 -> tamanho = "PP";
                            case 2 -> tamanho = "P";
                            case 3 -> tamanho = "M";
                            case 4 -> tamanho = "G";
                            case 5 -> tamanho = "GG";
                            case 6 -> tamanho = "XG";
                            default -> {
                                System.out.println("Opção inválida. Tente novamente.");
                                continue;
                            }
                        }
                        break;
                    } else {
                        System.out.println("Entrada inválida. Por favor, informe um número válido.");
                        sc.nextLine(); // Limpa o buffer
                    }
                }
            }

            // Ano
            System.out.print("Deseja filtrar pelo ano? (s/n): ");
            if (sc.nextLine().trim().equalsIgnoreCase("s")) {
                while (true) {
                    System.out.print("Informe o ano da camiseta: ");
                    if (sc.hasNextInt()) {
                        ano = sc.nextInt();
                        sc.nextLine(); // Limpa o buffer
                        if (String.valueOf(ano).length() == 4) {
                            break;
                        } else {
                            System.out.println("Ano inválido. Informe um ano com 4 dígitos.");
                        }
                    } else {
                        System.out.println("Entrada inválida. Por favor, informe um número.");
                        sc.nextLine(); // Limpa o buffer
                    }
                }
            }

            // Constrói a query SQL com os filtros selecionados
            StringBuilder queryBuilder = new StringBuilder("SELECT * FROM Produto WHERE 1=1");
            if (clube != null && !clube.isEmpty()) {
                queryBuilder.append(" AND Clube = ?");
            }
            if (codigo != null) {
                queryBuilder.append(" AND Cod = ?");
            }
            if (liga != null) {
                queryBuilder.append(" AND Liga = ?");
            }
            if (tamanho != null) {
                queryBuilder.append(" AND Tamanho = ?");
            }
            if (ano > 0) {
                queryBuilder.append(" AND Ano = ?");
            }
            sql = queryBuilder.toString();
        }

        // Executa a query com filtros configurados
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            int paramIndex = 1;
            if (aplicarFiltros) {
                if (clube != null && !clube.isEmpty()) {
                    stmt.setString(paramIndex++, clube);
                }
                if (codigo != null) {
                    stmt.setString(paramIndex++, codigo);
                }
                if (liga != null) {
                    stmt.setString(paramIndex++, liga);
                }
                if (tamanho != null) {
                    stmt.setString(paramIndex++, tamanho);
                }
                if (ano > 0) {
                    stmt.setInt(paramIndex++, ano);
                }
            }

            ResultSet rs = stmt.executeQuery();

            // Verificação para exibir mensagem caso o clube não seja encontrado
            if (!rs.isBeforeFirst()) {
                System.out.println("Clube não encontrado no estoque.");
                return;
            }

            // Exibição dos resultados
            System.out.println("==== Estoque de Camisetas ====");
            while (rs.next()) {
                System.out.println("Clube: " + rs.getString("Clube"));
                System.out.println("Liga: " + rs.getString("Liga"));
                System.out.println("Tamanho: " + rs.getString("Tamanho"));
                System.out.println("Ano: " + rs.getInt("Ano"));
                System.out.println("Código: " + rs.getString("Cod"));
                System.out.println("Preço: " + rs.getDouble("Preco"));
                System.out.println("Quantidade: " + rs.getInt("Quantidade"));
                System.out.println("----------------------------");
            }
            System.out.println("=============================");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Método para cadastrar camisetas
    private static void cadastrarCamisetas(Connection conn, Scanner sc) {
        String clubeEscolhido = "";
        String liga = "";
        // Seleção de liga
        while (true) {
            System.out.println("Informe a liga:");
            System.out.println("1 - Brasileirão");
            System.out.println("2 - Premier League");
            System.out.println("3 - Serie A");
            System.out.println("4 - Bundesliga");
            System.out.println("5 - La Liga");
            System.out.println("6 - Ligue 1");
            System.out.println("7 - Eredivisie");
            System.out.println("8 - Liga Portuguesa");
            System.out.println("9 - Liga Saudita");
            System.out.println("10 - MLS");
            System.out.println("11 - Seleção");
            System.out.println("0 - Voltar ao menu principal");

            int escolhaLiga = -1;

            // Verificando se a entrada é um número
            if (sc.hasNextInt()) {
                escolhaLiga = sc.nextInt();
                sc.nextLine(); // Limpar o buffer
            } else {
                sc.nextLine(); // Limpar o buffer de entrada inválida
                System.out.println("Por favor, insira um número válido para a liga.");
                continue; // Solicitar novamente a entrada
            }

            if (escolhaLiga < 0 || escolhaLiga > 11) {
                System.out.println("Opção inválida para a liga. Tente novamente.");
                continue;
            }

            switch (escolhaLiga) {
                case 0:
                    return;

                case 1: // Brasileirão
                    liga = "Brasileirão";
                    System.out.println("Escolha o clube:");
                    System.out.println("1 - Corinthians");
                    System.out.println("2 - São Paulo");
                    System.out.println("3 - Palmeiras");
                    System.out.println("4 - Santos");
                    System.out.println("5 - Flamengo");
                    System.out.println("6 - Vasco");
                    System.out.println("7 - Botafogo");
                    System.out.println("8 - Fluminense");
                    System.out.println("9 - Grêmio");
                    System.out.println("10 - Internacional");
                    System.out.println("11 - Atlético Mineiro");
                    System.out.println("12 - Cruzeiro");
                    System.out.println("0 - Voltar ao menu principal");

                    int clubeBrasileirao = -1;

                    if (sc.hasNextInt()) {
                        clubeBrasileirao = sc.nextInt();
                        sc.nextLine();
                    } else {
                        sc.nextLine();
                        System.out.println("Por favor, insira um número válido para o clube.");
                        continue;
                    }

                    if (clubeBrasileirao < 0 || clubeBrasileirao > 12) {
                        System.out.println("Opção inválida. Tente novamente.");
                        continue;
                    }
                    switch (clubeBrasileirao) {
                        case 0:
                            return;
                        case 1:
                            clubeEscolhido = "Corinthians";
                            break;
                        case 2:
                            clubeEscolhido = "São Paulo";
                            break;
                        case 3:
                            clubeEscolhido = "Palmeiras";
                            break;
                        case 4:
                            clubeEscolhido = "Santos";
                            break;
                        case 5:
                            clubeEscolhido = "Flamengo";
                            break;
                        case 6:
                            clubeEscolhido = "Vasco";
                            break;
                        case 7:
                            clubeEscolhido = "Botafogo";
                            break;
                        case 8:
                            clubeEscolhido = "Fluminense";
                            break;
                        case 9:
                            clubeEscolhido = "Grêmio";
                            break;
                        case 10:
                            clubeEscolhido = "Internacional";
                            break;
                        case 11:
                            clubeEscolhido = "Atlético Mineiro";
                            break;
                        case 12:
                            clubeEscolhido = "Cruzeiro";
                            break;
                        default:
                    }
                    break;

                case 2: // Premier League

                    liga = "Premier League";
                    System.out.println("Escolha o clube:");
                    System.out.println("1 - Arsenal");
                    System.out.println("2 - Manchester United");
                    System.out.println("3 - Manchester City");
                    System.out.println("4 - Liverpool");
                    System.out.println("5 - Chelsea");
                    System.out.println("0 - Voltar ao menu principal");

                    int clubePremierLeague = -1;

                    if (sc.hasNextInt()) { // Verificando se a entrada é um número
                        clubePremierLeague = sc.nextInt();
                        sc.nextLine();
                    } else {
                        sc.nextLine();
                        System.out.println("Por favor, insira um número válido para o clube.");
                        continue;
                    }
                    if (clubePremierLeague < 0 || clubePremierLeague > 5) {
                        System.out.println("Opção inválida. Tente novamente.");
                        continue;
                    }
                    switch (clubePremierLeague) {
                        case 0:
                            return;
                        case 1:
                            clubeEscolhido = "Arsenal";
                            break;
                        case 2:
                            clubeEscolhido = "Manchester United";
                            break;
                        case 3:
                            clubeEscolhido = "Manchester City";
                            break;
                        case 4:
                            clubeEscolhido = "Liverpool";
                            break;
                        case 5:
                            clubeEscolhido = "Chelsea";
                            break;
                        default:
                            System.out.println("Opção inválida. Tente novamente.");
                            continue;
                    }
                    break;
                case 3: // Serie A
                    System.out.println("Escolha o clube:");
                    System.out.println("1 - Milan");
                    System.out.println("2 - Inter de Milão");
                    System.out.println("3 - Juventus");
                    System.out.println("4 - Roma");
                    System.out.println("5 - Napoli");
                    System.out.println("0 - Voltar ao menu principal");

                    int clubeSerieA = -1;

                    if (sc.hasNextInt()) { // Verificando se a entrada é um número
                        clubeSerieA = sc.nextInt();
                        sc.nextLine();
                    } else {
                        sc.nextLine();
                        System.out.println("Por favor, insira um número válido para o clube.");
                        continue;
                    }
                    if (clubeSerieA < 0 || clubeSerieA > 5) {
                        System.out.println("Opção inválida. Tente novamente.");
                        continue;
                    }

                    switch (clubeSerieA) {
                        case 0:
                            return;
                        case 1:
                            clubeEscolhido = "Milan";
                            break;
                        case 2:
                            clubeEscolhido = "Inter de Milão";
                            break;
                        case 3:
                            clubeEscolhido = "Juventus";
                            break;
                        case 4:
                            clubeEscolhido = "Roma";
                            break;
                        case 5:
                            clubeEscolhido = "Napoli";
                            break;
                        default:
                            System.out.println("Opção inválida para o clube.");
                            return;
                    }
                    break;
                case 4: // Bundesliga
                    System.out.println("Escolha o clube:");
                    System.out.println("1 - Borussia Dortmund");
                    System.out.println("2 - Bayern Munchen");
                    System.out.println("3 - Bayer Leverkusen");
                    System.out.println("4 - RB Leipzig");
                    System.out.println("5 - Wolfsburg");
                    System.out.println("0 - Voltar ao menu principal");

                    int clubeBundesliga = -1;

                    if (sc.hasNextInt()) { // Verificando se a entrada é um número
                        clubeBundesliga = sc.nextInt();
                        sc.nextLine();
                    } else {
                        sc.nextLine();
                        System.out.println("Por favor, insira um número válido para o clube.");
                        continue;
                    }
                    if (clubeBundesliga < 0 || clubeBundesliga > 5) {
                        System.out.println("Opção inválida. Tente novamente.");
                        continue;
                    }
                    switch (clubeBundesliga) {
                        case 0:
                            return;
                        case 1:
                            clubeEscolhido = "Borussia Dortmund";
                            break;
                        case 2:
                            clubeEscolhido = "Bayern Munchen";
                            break;
                        case 3:
                            clubeEscolhido = "Bayer Leverkusen";
                            break;
                        case 4:
                            clubeEscolhido = "RB Leipzig";
                            break;
                        case 5:
                            clubeEscolhido = "Wolfsburg";
                            break;
                        default:
                            System.out.println("Opção inválida para o clube.");
                            return;
                    }
                    break;
                case 5: // La Liga
                    System.out.println("Escolha o clube:");
                    System.out.println("1 - Real Madrid");
                    System.out.println("2 - Barcelona");
                    System.out.println("3 - Atletico de Madrid");
                    System.out.println("0 - Voltar ao menu principal");

                    int clubeLaLiga = -1;

                    if (sc.hasNextInt()) { // Verificando se a entrada é um número
                        clubeLaLiga = sc.nextInt();
                        sc.nextLine();
                    } else {
                        sc.nextLine();
                        System.out.println("Por favor, insira um número válido para o clube.");
                        continue;
                    }
                    if (clubeLaLiga < 0 || clubeLaLiga > 3) {
                        System.out.println("Opção inválida. Tente novamente.");
                        continue;
                    }
                    switch (clubeLaLiga) {
                        case 0:
                            return;
                        case 1:
                            clubeEscolhido = "Real Madrid";
                            break;
                        case 2:
                            clubeEscolhido = "Barcelona";
                            break;
                        case 3:
                            clubeEscolhido = "Atletico de Madrid";
                            break;
                        default:
                            System.out.println("Opção inválida para o clube.");
                            return;
                    }
                    break;
                case 6: // Ligue 1
                    System.out.println("Escolha o clube:");
                    System.out.println("1 - PSG");
                    System.out.println("2 - Lyon");
                    System.out.println("3 - Olympique de Marseille");
                    System.out.println("0 - Voltar ao menu principal");

                    int clubeLigue1 = -1;

                    if (sc.hasNextInt()) { // Verificando se a entrada é um número
                        clubeLigue1 = sc.nextInt();
                        sc.nextLine();
                    } else {
                        sc.nextLine();
                        System.out.println("Por favor, insira um número válido para o clube.");
                        continue;
                    }
                    if (clubeLigue1 < 0 || clubeLigue1 > 3) {
                        System.out.println("Opção inválida. Tente novamente.");
                        continue;
                    }
                    switch (clubeLigue1) {
                        case 0:
                            return;
                        case 1:
                            clubeEscolhido = "PSG";
                            break;
                        case 2:
                            clubeEscolhido = "Lyon";
                            break;
                        case 3:
                            clubeEscolhido = "Olympique de Marseille";
                            break;
                        default:
                            System.out.println("Opção inválida para o clube.");
                            return;
                    }
                    break;
                case 7: // Eredivise
                    System.out.println("Escolha o clube:");
                    System.out.println("1 - Ajax");
                    System.out.println("2 - PSV");
                    System.out.println("3 - Feyenoord");
                    System.out.println("0 - Voltar ao menu principal");

                    int clubeEredivise = -1;

                    if (sc.hasNextInt()) { // Verificando se a entrada é um número
                        clubeEredivise = sc.nextInt();
                        sc.nextLine();
                    } else {
                        sc.nextLine();
                        System.out.println("Por favor, insira um número válido para o clube.");
                        continue;
                    }
                    if (clubeEredivise < 0 || clubeEredivise > 3) {
                        System.out.println("Opção inválida. Tente novamente.");
                        continue;
                    }
                    switch (clubeEredivise) {
                        case 0:
                            return;
                        case 1:
                            clubeEscolhido = "Ajax";
                            break;
                        case 2:
                            clubeEscolhido = "PSV";
                            break;
                        case 3:
                            clubeEscolhido = "Feyenoord";
                            break;
                        default:
                            System.out.println("Opção inválida para o clube.");
                            return;
                    }
                    break;
                case 8: // Liga Portuguesa
                    System.out.println("Escolha o clube:");
                    System.out.println("1 - Porto");
                    System.out.println("2 - Benfica");
                    System.out.println("3 - Sporting");
                    System.out.println("0 - Voltar ao menu principal");

                    int clubeLigaPortuguesa = -1;

                    if (sc.hasNextInt()) { // Verificando se a entrada é um número
                        clubeLigaPortuguesa = sc.nextInt();
                        sc.nextLine();
                    } else {
                        sc.nextLine();
                        System.out.println("Por favor, insira um número válido para o clube.");
                        continue;
                    }
                    if (clubeLigaPortuguesa < 0 || clubeLigaPortuguesa > 3) {
                        System.out.println("Opção inválida. Tente novamente.");
                        continue;
                    }
                    switch (clubeLigaPortuguesa) {
                        case 0:
                            return;
                        case 1:
                            clubeEscolhido = "Porto";
                            break;
                        case 2:
                            clubeEscolhido = "Benfica";
                            break;
                        case 3:
                            clubeEscolhido = "Sporting";
                            break;
                        default:
                            System.out.println("Opção inválida para o clube.");
                            return;
                    }
                    break;
                case 9: // Liga Saudita
                    System.out.println("Escolha o clube:");
                    System.out.println("1 - Al Hilal");
                    System.out.println("2 - Al-Nassr");
                    System.out.println("3 - Al-Ittihad");
                    System.out.println("0 - Voltar ao menu principal");

                    int clubeLigaSaudita = -1;

                    if (sc.hasNextInt()) { // Verificando se a entrada é um número
                        clubeLigaSaudita = sc.nextInt();
                        sc.nextLine();
                    } else {
                        sc.nextLine();
                        System.out.println("Por favor, insira um número válido para o clube.");
                        continue;
                    }
                    if (clubeLigaSaudita < 0 || clubeLigaSaudita > 3) {
                        System.out.println("Opção inválida. Tente novamente.");
                        continue;
                    }
                    switch (clubeLigaSaudita) {
                        case 0:
                            return;
                        case 1:
                            clubeEscolhido = "Al Hilal";
                            break;
                        case 2:
                            clubeEscolhido = "Al-Nassr";
                            break;
                        case 3:
                            clubeEscolhido = "Al-Ittihad";
                            break;
                        default:
                            System.out.println("Opção inválida para o clube.");
                            return;
                    }
                    break;
                case 10: // MLS
                    System.out.println("Escolha o clube:");
                    System.out.println("1 - Inter Miami");
                    System.out.println("2 - Los Angeles Galaxy");
                    System.out.println("3 - Orlando City");
                    System.out.println("0 - Voltar ao menu principal");

                    int clubeMLS = -1;

                    if (sc.hasNextInt()) { // Verificando se a entrada é um número
                        clubeMLS = sc.nextInt();
                        sc.nextLine();
                    } else {
                        sc.nextLine();
                        System.out.println("Por favor, insira um número válido para o clube.");
                        continue;
                    }
                    if (clubeMLS < 0 || clubeMLS > 3) {
                        System.out.println("Opção inválida. Tente novamente.");
                        continue;
                    }
                    switch (clubeMLS) {
                        case 0:
                            return;
                        case 1:
                            clubeEscolhido = "Inter Miami";
                            break;
                        case 2:
                            clubeEscolhido = "Los Angeles Galaxy";
                            break;
                        case 3:
                            clubeEscolhido = "Orlando City";
                            break;
                        default:
                            System.out.println("Opção inválida para o clube.");
                            return;
                    }
                    break;
                case 11: // Seleção
                    System.out.println("Escolha o clube:");
                    System.out.println("1 - Brasil");
                    System.out.println("2 - Argentina");
                    System.out.println("3 - Uruguai");
                    System.out.println("4 - Espanha");
                    System.out.println("5 - Itália");
                    System.out.println("6 - Inglaterra");
                    System.out.println("7 - França");
                    System.out.println("8 - Alemanha");
                    System.out.println("9 - Holanda");
                    System.out.println("10 - Portugal");
                    System.out.println("0 - Voltar ao menu principal");

                    int clubeSelecao = -1;

                    if (sc.hasNextInt()) { // Verificando se a entrada é um número
                        clubeSelecao = sc.nextInt();
                        sc.nextLine();
                    } else {
                        sc.nextLine();
                        System.out.println("Por favor, insira um número válido para o clube.");
                        continue;
                    }
                    if (clubeSelecao < 0 || clubeSelecao > 3) {
                        System.out.println("Opção inválida. Tente novamente.");
                        continue;
                    }
                    switch (clubeSelecao) {
                        case 0:
                            return;
                        case 1:
                            clubeEscolhido = "Brasil";
                            break;
                        case 2:
                            clubeEscolhido = "Argentina";
                            break;
                        case 3:
                            clubeEscolhido = "Uruguai";
                            break;
                        case 4:
                            clubeEscolhido = "Espanha";
                            break;
                        case 5:
                            clubeEscolhido = "Itália";
                            break;
                        case 6:
                            clubeEscolhido = "Inglaterra";
                            break;
                        case 7:
                            clubeEscolhido = "França";
                            break;
                        case 8:
                            clubeEscolhido = "Alemanha";
                            break;
                        case 9:
                            clubeEscolhido = "Holanda";
                            break;
                        case 10:
                            clubeEscolhido = "Portugal";
                            break;
                        default:
                            System.out.println("Opção inválida para o clube.");
                            return;
                    }
                    break;

                default:
                    System.out.println("Opção inválida para a liga. Tente novamente.");
                    continue;
            }
            break;
        }

        // Seleção de tamanho
        String tamanho = "";
        while (true) {
            System.out.println("Informe o tamanho:");
            System.out.println("1 - PP");
            System.out.println("2 - P");
            System.out.println("3 - M");
            System.out.println("4 - G");
            System.out.println("5 - GG");
            System.out.println("6 - XG");
            System.out.println("0 - Voltar ao menu principal");

            if (sc.hasNextInt()) {
                int escolhaTamanho = sc.nextInt();
                sc.nextLine(); // Limpa o buffer

                switch (escolhaTamanho) {
                    case 0:
                        return; // Volta ao menu principal
                    case 1:
                        tamanho = "PP";
                        break;
                    case 2:
                        tamanho = "P";
                        break;
                    case 3:
                        tamanho = "M";
                        break;
                    case 4:
                        tamanho = "G";
                        break;
                    case 5:
                        tamanho = "GG";
                        break;
                    case 6:
                        tamanho = "XG";
                        break;
                    default:
                        System.out.println("Opção inválida. Tente novamente.");
                        continue;
                }
                break;
            } else {
                System.out.println("Entrada inválida. Por favor, informe um número correspondente ao tamanho.");
                sc.nextLine();
            }
        }

        int ano;
        while (true) {
            System.out.print("Informe o ano: ");
            if (sc.hasNextInt()) {
                ano = sc.nextInt();
                sc.nextLine();

                // Verifica se o ano tem 4 dígitos e está dentro do intervalo permitido
                if (String.valueOf(ano).length() == 4 && ano >= 1830 && ano <= 2026) {
                    break;
                } else {
                    System.out.println("Erro: O ano deve ter 4 dígitos e estar entre 1830 e 2026.");
                }
            } else {
                System.out.println("Entrada inválida. Por favor, informe um número de 4 dígitos para o ano.");
                sc.nextLine();
            }
        }

        String cod;
        while (true) {
            System.out.print("Informe o código: ");
            cod = sc.nextLine();

            // Verifica se é apenas números e tem no máximo 3 dígitos
            if (cod.matches("\\d+") && cod.length() <= 3) {
                int codInt = Integer.parseInt(cod);

                // Verifica se está entre 1 e 999
                if (codInt < 1 || codInt > 999) {
                    System.out.println("Entrada inválida. O código deve ser um número entre 1 e 999.");
                    continue;
                }

                // Verifica se o código já está cadastrado
                if (codigoJaCadastrado(conn, cod)) {
                    System.out.println("O código '" + cod + "' já está cadastrado. Tente outro código.");
                    continue;
                }

                // Se todas as verificações passarem, sai do loop
                break;
            } else {
                System.out.println("Entrada inválida. O código deve conter apenas números e ter no máximo 3 dígitos.");
            }
        }

        double preco;
        while (true) {
            System.out.print("Informe o preço: ");
            String precoInput = sc.nextLine().trim();

            // Verifica se a entrada está no formato esperado (máximo 4 dígitos antes da
            // vírgula/ponto e 2 depois)
            if (precoInput.matches("\\d{1,4}([.,]\\d{1,2})?")) {
                // Substitui vírgula por ponto para padronizar
                precoInput = precoInput.replace(",", ".");
                preco = Double.parseDouble(precoInput);

                // Verifica se o preço é positivo e válido
                if (preco >= 0) {
                    break;
                } else {
                    System.out.println("O preço deve ser um valor positivo.");
                }
            } else {
                System.out.println("Entrada inválida. Informe um preço válido no formato 0000,00 ou 0.000,00.");
            }
        }

        int quantidade;
        while (true) {
            System.out.print("Informe a quantidade: ");
            String quantidadeInput = sc.nextLine().trim();

            // Verifica se a entrada contém apenas números
            if (quantidadeInput.matches("[0-9]+")) {
                quantidade = Integer.parseInt(quantidadeInput);
                break;
            } else {
                System.out.println("Entrada inválida. Informe apenas números.");
            }
        }

        String sql = "INSERT INTO Produto (Clube, Liga, Tamanho, Ano, Cod, Preco, Quantidade) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, clubeEscolhido);
            stmt.setString(2, liga);
            stmt.setString(3, tamanho);
            stmt.setInt(4, ano);
            stmt.setString(5, cod);
            stmt.setDouble(6, preco);
            stmt.setInt(7, quantidade);

            stmt.executeUpdate();
            System.out.println("Camiseta cadastrada com sucesso! Compra bem-sucedida");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        double valorTotal = quantidade * preco;
        System.out.println("\n======== Nota Fiscal ========");
        System.out.println("Quantidade de itens: " + quantidade);
        System.out.println("Valor unitário: R$ " + String.format("%.2f", preco));
        System.out.println("Camiseta: " + clubeEscolhido);
        System.out.println("Tamanho: " + tamanho);
        System.out.println("Ano: " + ano);
        System.out.println("Valor Total: R$ " + String.format("%.2f", valorTotal));
        System.out.println("=============================");

    }

    // Método para verificar se o código já foi cadastrado no banco de dados
    private static boolean codigoJaCadastrado(Connection conn, String cod) {
        String sql = "SELECT COUNT(*) FROM Produto WHERE Cod = ?";

        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, cod);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Método para gerenciar o estoque
    private static void gerenciarEstoque(Connection conn, Scanner sc) {
        System.out.println("Deseja adicionar ou remover camisetas? (adicionar/remover)");
        String operacao = sc.nextLine().toLowerCase();

        if (!operacao.equals("adicionar") && !operacao.equals("remover")) {
            System.out.println("Operação inválida. Retornando ao menu.");
            return;
        }

        System.out.print("Informe o código da camiseta: ");
        String codigo = sc.nextLine();

        String sqlBusca = "SELECT Clube, Preco, Quantidade, Tamanho, Ano FROM Produto WHERE Cod = ?";
        String sqlAtualiza = "UPDATE Produto SET Quantidade = ? WHERE Cod = ?";
        String clube;
        String tamanho;
        double valorUnitario;
        int quantidadeAtual;
        int ano;

        try (PreparedStatement stmtBusca = conn.prepareStatement(sqlBusca);
                PreparedStatement stmtAtualiza = conn.prepareStatement(sqlAtualiza)) {

            // Busca os dados do produto no banco de dados
            stmtBusca.setString(1, codigo);
            ResultSet rs = stmtBusca.executeQuery();

            if (rs.next()) {
                clube = rs.getString("Clube");
                valorUnitario = rs.getDouble("Preco");
                quantidadeAtual = rs.getInt("Quantidade");
                tamanho = rs.getString("Tamanho");
                ano = rs.getInt("Ano");

                System.out.print("Informe a quantidade: ");
                int quantidade;

                try {
                    quantidade = sc.nextInt();
                    sc.nextLine();
                } catch (InputMismatchException e) {
                    System.out.println("Quantidade inválida. Retornando ao menu.");
                    sc.nextLine();
                    return;
                }

                int novaQuantidade;
                boolean isDevolucao = false;

                // Verifica se é uma operação de compra ou devolução em caso de "adicionar"
                if (operacao.equals("adicionar")) {
                    System.out.println("Compra ou Devolução? (compra/devolucao)");
                    String tipoAdicao = sc.nextLine().toLowerCase();

                    if (tipoAdicao.equals("compra")) {
                        novaQuantidade = quantidadeAtual + quantidade;
                        System.out.println("Estoque atualizado, compra bem-sucedida!");
                    } else if (tipoAdicao.equals("devolucao")) {
                        novaQuantidade = quantidadeAtual + quantidade;
                        isDevolucao = true;
                        System.out.println("Estoque atualizado, devolução bem-sucedida!");
                    } else {
                        System.out.println("Operação inválida. Retornando ao menu.");
                        return;
                    }
                } else { // Operação de remoção
                    if (quantidade > quantidadeAtual) {
                        System.out.println("Estoque insuficiente para remoção. Operação cancelada.");
                        return;
                    }
                    novaQuantidade = quantidadeAtual - quantidade;
                    System.out.println("Estoque atualizado, venda bem-sucedida!");
                }

                // Atualiza o banco de dados com a nova quantidade
                stmtAtualiza.setInt(1, novaQuantidade);
                stmtAtualiza.setString(2, codigo);
                stmtAtualiza.executeUpdate();

                double valorTotal = valorUnitario * quantidade;

                System.out.println("======== Nota Fiscal ========");
                System.out.println("Quantidade de itens: " + quantidade);
                System.out.println("Valor unitário: R$ " + String.format("%.2f", valorUnitario));
                System.out.println("Camiseta: " + clube);
                System.out.println("Tamanho: " + tamanho);
                System.out.println("Ano: " + ano);
                System.out.println("Operação: " + (isDevolucao ? "Devolução" : "Compra"));
                System.out.println("Valor Total: R$ " + String.format("%.2f", valorTotal));
                System.out.println("=============================");

            } else {
                System.out.println("Código não encontrado no estoque.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Método para gerenciar usuários
    private static void gerenciarUsuarios(Connection conn, Scanner sc) {
        System.out.println("Escolha uma opção:");
        System.out.println("1 - Adicionar usuário");
        System.out.println("2 - Remover usuário");
        System.out.println("3 - Exibir informações dos usuários");

        int opcao;
        try {
            opcao = Integer.parseInt(sc.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Opção inválida. Retornando ao menu.");
            return;
        }

        switch (opcao) {
            case 1:
                adicionarUsuario(conn, sc);
                break;
            case 2:
                removerUsuario(conn, sc);
                break;
            case 3:
                exibirUsuarios(conn);
                break;
            default:
                System.out.println("Opção inválida. Retornando ao menu.");
                break;
        }
    }

    // Método para exibir informações dos usuários
    private static void exibirUsuarios(Connection conn) {
        String sql = "SELECT Nome, Login, Senha FROM Usuario";

        try (PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {

            System.out.println("==== Informações dos Usuários ====");
            while (rs.next()) {
                String nome = rs.getString("Nome");
                String login = rs.getString("Login");
                String senha = rs.getString("Senha");

                System.out.println("Nome: " + nome);
                System.out.println("Login: " + login);
                System.out.println("Senha: " + senha);
                System.out.println("---------------------------------");
            }
            System.out.println("=================================");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void adicionarUsuario(Connection conn, Scanner sc) {
        String nome;
        while (true) {
            System.out.print("Informe o nome do novo funcionário: ");
            nome = sc.nextLine().trim();

            // Verifica se o nome não está vazio, contém apenas letras e não ultrapassa 100
            // caracteres
            if (!nome.isEmpty() && nome.matches("[a-zA-Z\\s]+") && nome.length() <= 100) {
                break;
            } else if (nome.length() > 100) {
                System.out.println("Nome muito longo. O nome deve ter no máximo 100 caracteres.");
            } else {
                System.out.println("Nome inválido. Informe um nome apenas com letras e não deixe o campo vazio.");
            }
        }

        String cpf;
        while (true) {
            System.out.print("Informe o CPF: ");
            cpf = sc.nextLine().trim();

            // Verifica se o CPF não está vazio, contém apenas números e tem 11 dígitos
            if (!cpf.isEmpty() && cpf.matches("\\d{11}")) {
                // Verifica se o CPF já existe no banco de dados
                if (cpfJaCadastrado(conn, cpf)) {
                    System.out.println("CPF já cadastrado. Tente outro CPF.");
                } else {
                    break;
                }
            } else {
                System.out.println("CPF inválido. Informe um CPF válido com 11 dígitos numéricos.");
            }
        }

        String telefone;
        while (true) {
            System.out.print("Informe o telefone: ");
            telefone = sc.nextLine().trim();

            // Verifica se o telefone não está vazio, contém apenas números e tem no máximo
            // 11 dígitos
            if (!telefone.isEmpty() && telefone.matches("\\d{1,11}")) {
                // Verifica se o telefone já existe no banco de dados
                if (telefoneJaCadastrado(conn, telefone)) {
                    System.out.println("Telefone já cadastrado. Tente outro número.");
                } else {
                    break;
                }
            } else {
                System.out.println("Telefone inválido. Informe um telefone válido com até 11 dígitos numéricos.");
            }
        }

        String login;
        while (true) {
            System.out.print("Informe o login: ");
            login = sc.nextLine().trim();

            // Verifica se o login não está vazio, contém até 20 caracteres, um '@' e
            // termina com 'adm' ou 'fun'
            if (!login.isEmpty() && login.length() <= 20
                    && login.contains("@")
                    && (login.endsWith("adm") || login.endsWith("fun"))
                    && login.indexOf('@') == login.lastIndexOf('@')) {

                // Verifica se o login já existe no banco de dados
                if (loginJaCadastrado(conn, login)) {
                    System.out.println("Login já cadastrado. Tente outro login.");
                } else {
                    break;
                }
            } else {
                System.out.println(
                        "Login inválido. O login deve conter no máximo 20 caracteres, exatamente um '@' e terminar com 'adm' ou 'fun'.");
            }
        }

        String senha;
        while (true) {
            System.out.print("Informe a senha: ");
            senha = sc.nextLine().trim();

            // Verifica se a senha não está vazia e tem no máximo 20 caracteres
            if (!senha.isEmpty() && senha.length() <= 20 && senha.matches("[a-zA-Z0-9]+")) {
                break;
            } else {
                System.out.println(
                        "Senha inválida. A senha deve ter no máximo 20 caracteres e conter apenas letras e números.");
            }
        }

        boolean adm = false;
        while (true) {
            System.out.print("É administrador? (s/n): ");
            String entrada = sc.nextLine().toLowerCase();

            if (entrada.equals("s")) {
                adm = true;
                break;
            } else if (entrada.equals("n")) {
                adm = false;
                break;
            } else {
                System.out.println("Entrada inválida. Digite 's' para sim ou 'n' para não.");
            }
        }

        String sql = "INSERT INTO Usuario (Nome, CPF, Telefone, Login, Senha, ADM) VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nome);
            stmt.setString(2, cpf);
            stmt.setString(3, telefone);
            stmt.setString(4, login);
            stmt.setString(5, senha);
            stmt.setBoolean(6, adm);

            stmt.executeUpdate();
            System.out.println("Usuário cadastrado com sucesso!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static boolean loginJaCadastrado(Connection conn, String login) {
        String sql = "SELECT COUNT(*) FROM Usuario WHERE Login = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, login);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0; // Retorna true se o login já existe
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private static boolean cpfJaCadastrado(Connection conn, String cpf) {
        String sql = "SELECT COUNT(*) FROM Usuario WHERE CPF = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, cpf);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private static boolean telefoneJaCadastrado(Connection conn, String telefone) {
        String sql = "SELECT COUNT(*) FROM Usuario WHERE Telefone = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, telefone);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0; // Retorna true se o telefone já existe
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Método para remover usuário
    private static void removerUsuario(Connection conn, Scanner sc) {
        System.out.print("Informe o login do usuário a ser removido: ");
        String login = sc.nextLine();

        if (login.equalsIgnoreCase("michel@adm")) {
            System.out.println("O usuário 'michel@adm' não pode ser removido.");
            return;
        }

        String sql = "DELETE FROM Usuario WHERE Login = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, login);

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Usuário removido com sucesso!");
            } else {
                System.out.println("Usuário não encontrado. Verifique o login.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
