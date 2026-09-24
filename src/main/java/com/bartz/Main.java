package com.bartz;

import javax.swing.SwingUtilities;

import com.bartz.database.DbManager;
import com.bartz.ui.TelaPrincipal;
import com.formdev.flatlaf.FlatLightLaf;

public class Main {
    public static void main(String[] args){
        
        // ativa o tema do FlatLaf
        FlatLightLaf.setup(); // Configura o tema

        // inicia o banco de dados
        DbManager.iniciarBanco();

        // 
        SwingUtilities.invokeLater(() -> {
            TelaPrincipal tela = new TelaPrincipal();
            tela.setVisible(true);
        });
    }
}
