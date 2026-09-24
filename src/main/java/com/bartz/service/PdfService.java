package com.bartz.service;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.text.PDFTextStripper;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class PdfService {
    // Caminho da pasta dos PDFs
    private static final String PASTA_DESENHOS = "\\\\192.168.1.10\\DatabaseFolder\\Desenhos";


    /**
     * BLOCO 1: Busca o arquivo PDF na pasta de rede.
     * Procura um arquivo cujo nome seja exatamente o código bipado ou contenha o código.
     */
    public static File buscaPDF(String codigo){
        String nomePasta = codigo.substring(0, 3).trim().toUpperCase();
        File pasta = new File(PASTA_DESENHOS, nomePasta);

        // Se a rede estiver fora
        if(!pasta.isDirectory()){
            System.err.println("[ERRO] Acesso ao servidor indisponivel: " + PASTA_DESENHOS);
            return null;
        }

        // Encontra o pdf pelo nome
        File arquivo = new File(pasta, codigo + ".pdf");
        if(arquivo.exists()){
            return arquivo;
        }

        return null;
    }


    /**
     * BLOCO 2: Renderiza a primeira página do PDF em uma imagem Java (BufferedImage).
     * O PDFBox transforma as linhas e vetores do PDF em pixels de alta qualidade.
     */
    public static BufferedImage pdfToImage(File arquivoPdf) throws IOException{
        
        // Abre o PDF com o PDFBox
        try(PDDocument documento = Loader.loadPDF(arquivoPdf)){
            PDFRenderer render = new PDFRenderer(documento);

            // 150 dpi pra uma imagem nítida
            return render.renderImageWithDPI(0, 150);
        }
    }


    /**
     * BLOCO 3: Lê o texto dentro do PDF e descobre a quantidade de bordas!
     * As bordas são identificadas como B1, B2, B3, B4...
     * Essa função usa uma 'Expressão Regular' (Regex) para encontrar e contar essas siglas.
     */
    public static int mostrarBordas(File pdf){
        try(PDDocument documento = Loader.loadPDF(pdf)){

            // PDFTextStripper é pra extrair o texto
            PDFTextStripper stripper = new PDFTextStripper();
            String texto = stripper.getText(documento);

            // Código para procurar pela borda 'B'
            Pattern pattern = Pattern.compile("\\bB[1-4]\\b", Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(texto);

            int totalBordas = 0;
            while (matcher.find()){
                totalBordas++;
            }

            return totalBordas > 0 ? totalBordas : 1;
        }
        catch(IOException e){
            System.err.println("Não foi possível extrair texto do PDF: " + e.getMessage());
            return 1;
        }
    }
}
