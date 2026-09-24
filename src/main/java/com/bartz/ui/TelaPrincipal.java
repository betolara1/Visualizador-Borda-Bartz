package com.bartz.ui;

import com.bartz.database.DbManager;
import com.bartz.model.Dados;
import com.bartz.service.PdfService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;

public class TelaPrincipal extends JFrame{

    // componentes interface
    private JTextField txtCodigo;
    private JLabel lblImagemPDF;
    private JLabel lblNomeArquivo;
    private JLabel lblQtdTotal;
    private JLabel lblQtdBipada;
    private JLabel lblStatus;
    private JButton btnContarBorda;


    // variaveis de controle das peças atuais
    private File arquivo;
    private int totalBordas;
    private int bordasBipadas;

    public TelaPrincipal(){
        super("Bartz Analisador de Bordas");
        configurarJanela();
        criarComponentes();
    }


    /**
     * BLOCO Configurações gerais da Janela (Tamanho, fechamento, tela cheia)
     */
    private void configurarJanela(){
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
        setSize(1280,800); // resolução padrão
        setLocationRelativeTo(null); // centraliza no windows
        setLayout(new BorderLayout(10, 10)); // espaçamento 10px entre as areas
    }


    /**
     * BLOCO Construção visual dos painéis
     */
    private void criarComponentes() {

        // --- 1. TOPO (NORTE): Campo de Bipagem ---
        JPanel painelTopo = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        painelTopo.setBorder(new EmptyBorder(5, 10, 5, 10));
        JLabel lblInstrucao = new JLabel("Bipe o Código / QR Code:");
        lblInstrucao.setFont(new Font("Segoe UI", Font.BOLD, 16));
        txtCodigo = new JTextField(20);
        txtCodigo.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        
        lblStatus = new JLabel("Aguardando bipagem...");
        lblStatus.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        lblStatus.setForeground(Color.GRAY);
        painelTopo.add(lblInstrucao);
        painelTopo.add(txtCodigo);
        painelTopo.add(lblStatus);
        add(painelTopo, BorderLayout.NORTH);


        // --- 2. CENTRO: Onde o PDF é exibido ---
        lblImagemPDF = new JLabel("Nenhum PDF carregado", SwingConstants.CENTER);
        lblImagemPDF.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblImagemPDF.setForeground(Color.LIGHT_GRAY);

        // JScrollPane permite dar rolagem com a rodinha do mouse se o PDF for grande
        JScrollPane scrollPdf = new JScrollPane(lblImagemPDF);
        scrollPdf.getVerticalScrollBar().setUnitIncrement(16); // Rolagem mais suave
        add(scrollPdf, BorderLayout.CENTER);


        // --- 3. LATERAL DIREITA (LESTE): Placar de Bordas e Informações ---
        JPanel painelLateral = new JPanel();
        painelLateral.setLayout(new BoxLayout(painelLateral, BoxLayout.Y_AXIS));
        painelLateral.setBorder(new EmptyBorder(20, 20, 20, 20));
        painelLateral.setPreferredSize(new Dimension(300, 0));
        JLabel lblTituloLateral = new JLabel("Controle da Peça");
        lblTituloLateral.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblNomeArquivo = new JLabel("Arquivo: -");
        lblNomeArquivo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblQtdTotal = new JLabel("Bordas a passar: 0");
        lblQtdTotal.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblQtdTotal.setForeground(new Color(0, 102, 204)); // Azul profissional
        lblQtdBipada = new JLabel("Bordas passadas: 0");
        lblQtdBipada.setFont(new Font("Segoe UI", Font.BOLD, 18));

        btnContarBorda = new JButton("Passar Borda (+1)");
        btnContarBorda.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnContarBorda.setEnabled(false);
        painelLateral.add(lblTituloLateral);
        painelLateral.add(Box.createVerticalStrut(15));
        painelLateral.add(lblNomeArquivo);
        painelLateral.add(Box.createVerticalStrut(15));
        painelLateral.add(lblQtdTotal);
        painelLateral.add(Box.createVerticalStrut(10));
        painelLateral.add(lblQtdBipada);
        painelLateral.add(Box.createVerticalStrut(25));
        painelLateral.add(btnContarBorda);
        add(painelLateral, BorderLayout.EAST);


        // --- 4. EVENTOS (A inteligência da tela) ---
        // O leitor de código de barras dispara um ENTER ao final da leitura:
        txtCodigo.addActionListener(e -> processarBipagem());

        // Ação ao clicar no botão de passar borda
        btnContarBorda.addActionListener(e -> registrarPassagemBorda());
    }


    /**
     * BLOCO Processa o código digitado ou bipado pelo leitor
     */
    private void processarBipagem() {
        String codigo = txtCodigo.getText().trim();

        if (codigo.isEmpty()) {
            return;
        }

        lblStatus.setText("Buscando arquivo...");
        lblStatus.setForeground(Color.BLUE);

        // Busca o PDF usando o PdfService.java
        arquivo = PdfService.buscaPDF(codigo);

        if (arquivo == null) {
            lblStatus.setText("PDF não encontrado para o código: " + codigo);
            lblStatus.setForeground(Color.RED);
            txtCodigo.selectAll();
            return;
        }

        // Se achou o arquivo:
        try {
            
            // Converte o PDF em imagem e coloca no JLabel
            BufferedImage img = PdfService.pdfToImage(arquivo);
            lblImagemPDF.setText(""); // Limpa o texto "Nenhum PDF carregado"
            lblImagemPDF.setIcon(new ImageIcon(img));

            // Extrai a quantidade de bordas do próprio PDF
            totalBordas = PdfService.mostrarBordas(arquivo);
            bordasBipadas = 1; // Primeira passagem da peça

            // Atualiza os textos da tela
            lblNomeArquivo.setText("Arquivo: " + arquivo.getName());
            lblQtdTotal.setText("Bordas a passar: " + totalBordas);
            lblQtdBipada.setText("Bordas passadas: " + bordasBipadas);
            btnContarBorda.setEnabled(true);
            lblStatus.setText("Peça carregada com sucesso!");
            lblStatus.setForeground(new Color(34, 139, 34));

            // Salva o registro da bipagem no SQLite
            salvarHistoricoNoBanco(codigo);

        } 
        catch (IOException ex) {
            lblStatus.setText("Erro ao renderizar PDF: " + ex.getMessage());
            lblStatus.setForeground(Color.RED);
        } 
        finally {
            // Limpa e foca no campo para a próxima bipagem
            txtCodigo.setText("");
            txtCodigo.requestFocusInWindow();
        }
    }

    /**
     * BLOCO Incrementa a quantidade de bordas passadas
     */
    private void registrarPassagemBorda() {
        if (arquivo == null) return;
        bordasBipadas++;
        lblQtdBipada.setText("Bordas passadas: " + bordasBipadas);

        // Atualiza o registro no banco com a nova quantidade
        String codigo = arquivo.getName().replace(".pdf", "");
        salvarHistoricoNoBanco(codigo);

        if (bordasBipadas >= totalBordas) {
            lblStatus.setText("Peça 100% finalizada!");
            lblStatus.setForeground(new Color(34, 139, 34));
            btnContarBorda.setEnabled(false);
        }
    }

    /**
     * BLOCO Grava no SQLite usando o nosso DbManager
     */
    private void salvarHistoricoNoBanco(String codigo) {
        Dados registro = new Dados(
            null, // O ID é gerado automaticamente pelo AUTOINCREMENT do SQLite
            codigo,
            arquivo.getName(),
            totalBordas,
            bordasBipadas,
            LocalDateTime.now()
        );

        DbManager.salvarDados(registro);
    }
    
}
