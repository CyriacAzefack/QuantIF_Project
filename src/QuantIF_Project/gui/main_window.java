/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package QuantIF_Project.gui;


import QuantIF_Project.patient.exceptions.BadParametersException;
import QuantIF_Project.patient.exceptions.DicomFilesNotFoundException;
import QuantIF_Project.patient.exceptions.NotDirectoryException;
import QuantIF_Project.serie.TEPSerie;
import QuantIF_Project.patient.PatientMultiSeries;
import QuantIF_Project.patient.exceptions.NoTAPSerieFoundException;
import QuantIF_Project.serie.TAPSerie;
import QuantIF_Project.patient.exceptions.PatientStudyException;
import QuantIF_Project.patient.exceptions.SeriesOrderException;
import QuantIF_Project.serie.Serie;
import ij.IJ;
import java.awt.Component;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;



/**
 *
 * @author Cyriac
 */
public class main_window extends javax.swing.JFrame {
    
       
    /**
     * TEPSerie en cours d'observation
     */
    private Serie patient;
    
    private PatientMultiSeries patientMultiSeries;
    
    /**
     * Verrou de synchronisation
     */
    private static Object lock;
    
    /**
     * Creates new form main_window
     */
    public main_window() {
        initComponents();
        this.patient = null;
        this.patientMultiSeries = null;
        
        main_window.lock = new Object();
        //On ferme toutes instances de IJ
        IJ.run("Close All");
        
        
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        patientChooser = new javax.swing.JFileChooser();
        dynSerieChooser = new javax.swing.JFileChooser();
        statSerieChooser = new javax.swing.JFileChooser();
        desktop = new javax.swing.JDesktopPane();
        jScrollPane1 = new javax.swing.JScrollPane();
        patientDescriptTextField = new javax.swing.JTextArea();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        openTEPSerie = new javax.swing.JMenuItem();
        openTAPSerie = new javax.swing.JMenuItem();
        closeAllSeries = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JPopupMenu.Separator();
        openMultiAcqMenu = new javax.swing.JMenuItem();
        closeMultiAcqMenu = new javax.swing.JMenuItem();
        jSeparator3 = new javax.swing.JPopupMenu.Separator();
        closeAllMenu = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        displayImagesMenu = new javax.swing.JMenuItem();

        patientChooser.setDialogTitle("Selectionnez un dossier patient");
        patientChooser.setFileHidingEnabled(false);

        dynSerieChooser.setDialogTitle("Sélectionnez une série dynamique");
        dynSerieChooser.setFileHidingEnabled(false);

        statSerieChooser.setDialogTitle("Selectionnez la série statique");
        statSerieChooser.setFileHidingEnabled(false);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("QuantIF_Project");
        setAutoRequestFocus(false);
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        desktop.setLayout(new java.awt.BorderLayout());

        jScrollPane1.setFocusable(false);
        jScrollPane1.setOpaque(false);
        jScrollPane1.setRequestFocusEnabled(false);
        jScrollPane1.setVerifyInputWhenFocusTarget(false);

        patientDescriptTextField.setEditable(false);
        patientDescriptTextField.setColumns(20);
        patientDescriptTextField.setFont(new java.awt.Font("Lucida Console", 0, 14)); // NOI18N
        patientDescriptTextField.setLineWrap(true);
        patientDescriptTextField.setRows(5);
        patientDescriptTextField.setText("PAS DE PATIENT EN COURS");
        patientDescriptTextField.setAutoscrolls(false);
        patientDescriptTextField.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        patientDescriptTextField.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jScrollPane1.setViewportView(patientDescriptTextField);

        jMenu1.setText("Application");
        jMenu1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenu1ActionPerformed(evt);
            }
        });

        openTEPSerie.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.CTRL_MASK));
        openTEPSerie.setText("Ouvrir une série TEP");
        openTEPSerie.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openTEPSerieActionPerformed(evt);
            }
        });
        jMenu1.add(openTEPSerie);

        openTAPSerie.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.CTRL_MASK));
        openTAPSerie.setText("Ouvrir une série TAP");
        openTAPSerie.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openTAPSerieActionPerformed(evt);
            }
        });
        jMenu1.add(openTAPSerie);

        closeAllSeries.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F, java.awt.event.InputEvent.CTRL_MASK));
        closeAllSeries.setText("Fermer série en cours");
        closeAllSeries.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeAllSeriesActionPerformed(evt);
            }
        });
        jMenu1.add(closeAllSeries);
        jMenu1.add(jSeparator2);

        openMultiAcqMenu.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        openMultiAcqMenu.setText("Ouvrir une Multi Acquisition");
        openMultiAcqMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openMultiAcqMenuActionPerformed(evt);
            }
        });
        jMenu1.add(openMultiAcqMenu);

        closeMultiAcqMenu.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F, java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        closeMultiAcqMenu.setText("Fermer une Multi Acquisition");
        closeMultiAcqMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeMultiAcqMenuActionPerformed(evt);
            }
        });
        jMenu1.add(closeMultiAcqMenu);
        jMenu1.add(jSeparator3);

        closeAllMenu.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F4, java.awt.event.InputEvent.ALT_MASK));
        closeAllMenu.setText("Quitter");
        closeAllMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeAllMenuActionPerformed(evt);
            }
        });
        jMenu1.add(closeAllMenu);

        jMenuBar1.add(jMenu1);

        jMenu2.setText("Options");

        displayImagesMenu.setText("Afficher Images");
        displayImagesMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                displayImagesMenuActionPerformed(evt);
            }
        });
        jMenu2.add(displayImagesMenu);

        jMenuBar1.add(jMenu2);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 836, Short.MAX_VALUE)
            .addComponent(desktop, javax.swing.GroupLayout.Alignment.TRAILING)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(desktop)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void closeAllMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeAllMenuActionPerformed
        this.dispose();
    }//GEN-LAST:event_closeAllMenuActionPerformed

    private void jMenu1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenu1ActionPerformed
                                  
    }//GEN-LAST:event_jMenu1ActionPerformed

    private void openTEPSerieActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openTEPSerieActionPerformed
        
        
        if (this.patient != null || this.patientMultiSeries != null ) 
            this.closeAllSeriesActionPerformed(evt);
        
        
            
        
        this.patientDescriptTextField.setText("Ouverture du patient en cours...");
        int returnVal = patientChooser.showOpenDialog(this);
        
        if (JFileChooser.APPROVE_OPTION == returnVal) {
            
            File choosenFile;
            choosenFile = patientChooser.getSelectedFile();
           
            // Un fois le dossier choisi
            
            if (choosenFile != null) {
                String patientDirPath;
                if (choosenFile.isDirectory()) {
                    patientDirPath = choosenFile.getAbsolutePath();
                }
                else {
                    patientDirPath = choosenFile.getParent();
                }
                
                
                //On peut créer un nouveau patient
                
               
		try {
			this.patient = new TEPSerie(patientDirPath);
                        JOptionPane.showMessageDialog(null, "Le dossier patient a été ouvert avec succès\n\n"+this.patient.toString(), "Info", JOptionPane.PLAIN_MESSAGE);
                        this.patientDescriptTextField.setText(this.patient.toString());
                        this.displayImagesMenuActionPerformed(evt);
                        
                        
                        System.out.println(this.patient);
		} catch (NotDirectoryException | DicomFilesNotFoundException
                        | BadParametersException e) {
			// TODO Auto-generated catch block
                   
			JOptionPane.showMessageDialog(null, e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
                        this.patientDescriptTextField.setText("PAS DE PATIENT EN COURS");
                        this.openTEPSerieActionPerformed(evt);
		} 
                
		
                
            }
            else {
                JOptionPane.showMessageDialog(null, "Problem accessing the file!", "Erreur", JOptionPane.ERROR_MESSAGE);
                this.patientDescriptTextField.setText("PAS DE PATIENT EN COURS");
            }
           
        } else {
            System.out.println("File access cancelled by user.");
            this.patientDescriptTextField.setText("PAS DE PATIENT EN COURS");
        }
    }//GEN-LAST:event_openTEPSerieActionPerformed

    private void displayImagesMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_displayImagesMenuActionPerformed
        
        if (this.patient != null) {
            
            if (patient instanceof TAPSerie) {
                TAPSerieViewer tsv = new TAPSerieViewer((TAPSerie) this.patient);
                tsv.setVisible(true);
                tsv.setVisible(true);
                this.desktop.add(tsv);
                this.desktop.setSelectedFrame(tsv);
            }
            else {

                PatientSerieViewer psv = new PatientSerieViewer(this.patient);
                psv.setVisible(true);


                //psv.setSize(this.viewerLabel.getSize());
                //psv.setSize(this.viewerLabel.getSize());
                psv.setVisible(true);
                this.desktop.add(psv);
                this.desktop.setSelectedFrame(psv);
            }
            
            
        }
        else if (this.patientMultiSeries != null) {
            PatientSerieViewer psv = new PatientSerieViewer(this.patientMultiSeries);
            psv.show();
            //Pour rendre la fenêtre immobile
            
            //psv.setSize(this.viewerLabel.getSize());
            psv.setVisible(true);
            this.desktop.add(psv);
            this.desktop.setSelectedFrame(psv);
        }
        else {
            JOptionPane.showMessageDialog(null, "Aucun patient sélectionné ", "Erreur", JOptionPane.ERROR_MESSAGE);

        }
    }//GEN-LAST:event_displayImagesMenuActionPerformed

    private void closeAllSeriesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeAllSeriesActionPerformed
        Component[] components = this.desktop.getComponents();
        for (Component component : components) {

            this.desktop.remove(component);
            this.desktop.validate();
            this.desktop.repaint();
            
        }
        this.patient = null;
        this.patientMultiSeries = null;
        JOptionPane.showMessageDialog(null, "L'acquisition en cours a été fermée", "Infos", JOptionPane.INFORMATION_MESSAGE);
        this.patientDescriptTextField.setText("PAS DE PATIENT EN COURS");
    }//GEN-LAST:event_closeAllSeriesActionPerformed

    private void openMultiAcqMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openMultiAcqMenuActionPerformed
        //On ferme le patient ou la multi acquisition ouvert avant
        if (this.patient != null) 
            this.closeAllSeriesActionPerformed(evt);
        
        if (this.patientMultiSeries != null)
            this.closeMultiAcqMenuActionPerformed(evt);
        
        this.patientDescriptTextField.setText("Ouverture de la multi acquisition en cours...\n");
        
        //On ouvre les différentes série une par une 
        
        //SERIE DYNAMIQUE DE DEPART
        TEPSerie startDynSerie; 
        this.patientDescriptTextField.append("Ouverture de la première série dynamique en cours...\n");
        startDynSerie = chooseTEPSerie(true);
        
        //SERIE STATIQUE
        TAPSerie staticSerie;
        this.patientDescriptTextField.append("Ouverture de la série statique en cours...\n");
        staticSerie = chooseTAPSerie();
        
       
        TAPSerieViewer tsv = new TAPSerieViewer(staticSerie);
        


        //tsv.setSize(this.viewerLabel.getSize());
        //this.viewerLabel.removeAll();
        

        //Thread gérant l'attente du choix de la coupe corporelle
        Thread thread = new Thread("Choosing Body block Thread") {
            public void run() {
                synchronized(lock) {
                    while (tsv.isVisible()) {
                        try {
                            System.out.println("Waiting choice...");
                            lock.wait();

                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    System.out.println("INIT SERIE TAP fini ");

                }
            }
        };
        


        System.out.println("Setting the unlock!!");
        
       //On debloque le vérrou a la fermeture de la fenêtre de choix de coupe corporelle
        tsv.addInternalFrameListener(new InternalFrameAdapter () {
            
            @Override
            public void internalFrameClosed(InternalFrameEvent e) {
                System.out.println("TAPSerie viewer is closing!!");
                synchronized (lock) {
                    tsv.setVisible(false);
                    
                    lock.notify();
                }
            }

        });
        tsv.setVisible(true);
        this.desktop.add(tsv);
        thread.start();
        //On attends que le thread meure
        
        
        
        //SERIE DYNAMIQUE DE FIN
       
        Thread dynamicChoiceThread = new Thread("Dynamic waiting thread") {
           
            /**
             * Attends le thread de la TAP avant de commencer celui de la dernière série dynamique
             */
            @Override
            public void run() {
                try {
                    thread.join();
                    patientDescriptTextField.append("Ouverture de la dernière série dynamique en cours...\n");
                    TEPSerie endDynSerie = chooseTEPSerie(false);
                    
                    if ((startDynSerie != null) && (staticSerie != null) && (endDynSerie != null)) {
                        try {
                            patientMultiSeries = new PatientMultiSeries(startDynSerie, staticSerie, endDynSerie);
                            JOptionPane.showMessageDialog(null, "La multi-acquisition a été ouverte avec succès", "Info", JOptionPane.PLAIN_MESSAGE);
                            displayImagesMenuActionPerformed(evt);
                        } catch (SeriesOrderException ex) {
                            JOptionPane.showMessageDialog(null, ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
                            patientDescriptTextField.setText("PAS DE PATIENT EN COURS");
                        } catch (PatientStudyException ex) {
                            Logger.getLogger(main_window.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    } else {
                        System.out.println("PEUT PAS OUVRIR MULTI ACQ (Une série est \"null\") ");
                    }
                } catch (InterruptedException ex) {
                    Logger.getLogger(main_window.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

        };
        System.out.println("Dynamic choice thread starting!");
        dynamicChoiceThread.start();
       
        
        
        
      
        
    }//GEN-LAST:event_openMultiAcqMenuActionPerformed

    private void closeMultiAcqMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeMultiAcqMenuActionPerformed
        this.patientMultiSeries = null;
    }//GEN-LAST:event_closeMultiAcqMenuActionPerformed

    private void openTAPSerieActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openTAPSerieActionPerformed
        if (this.patient != null || this.patientMultiSeries != null ) 
            this.closeAllSeriesActionPerformed(evt);
        
        
            
        
        this.patientDescriptTextField.setText("Ouverture de la Série en cours...");
        int returnVal = patientChooser.showOpenDialog(this);
        
        if (JFileChooser.APPROVE_OPTION == returnVal) {
            
            File choosenFile;
            choosenFile = patientChooser.getSelectedFile();
           
            // Un fois le dossier choisi
            
            if (choosenFile != null) {
                String patientDirPath;
                if (choosenFile.isDirectory()) {
                    patientDirPath = choosenFile.getAbsolutePath();
                }
                else {
                    patientDirPath = choosenFile.getParent();
                }
                
                
                //On peut créer un nouveau patient
                
               
		try {
			this.patient = new TAPSerie(patientDirPath);
                        JOptionPane.showMessageDialog(null, "La série TAP corps entier a été ouverte avec succès\n\n"+this.patient.toString(), "Info", JOptionPane.PLAIN_MESSAGE);
                        this.patientDescriptTextField.setText(this.patient.toString());
                        this.displayImagesMenuActionPerformed(evt);
                        
                        
                        System.out.println(this.patient);
		} catch (NotDirectoryException | DicomFilesNotFoundException
                        | BadParametersException e) {
			// TODO Auto-generated catch block
                   
			JOptionPane.showMessageDialog(null, e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
                        this.patientDescriptTextField.setText("PAS DE PATIENT EN COURS");
                        this.openTEPSerieActionPerformed(evt);
		} catch (NoTAPSerieFoundException ex) { 
                    Logger.getLogger(main_window.class.getName()).log(Level.SEVERE, null, ex);
                } 
                
		
                
            }
            else {
                JOptionPane.showMessageDialog(null, "Problem accessing the file!", "Erreur", JOptionPane.ERROR_MESSAGE);
                this.patientDescriptTextField.setText("PAS DE PATIENT EN COURS");
            }
           
        } else {
            System.out.println("File access cancelled by user.");
            this.patientDescriptTextField.setText("PAS DE PATIENT EN COURS");
        }
    }//GEN-LAST:event_openTAPSerieActionPerformed
    /**
     * Ouvre une fenêtre pour choisir une série de patient
     * @param dynChoose Si vaut true alors on ouvre une série dynamique, sinon une série statique
     * @return TEPSerie
     */
    private TEPSerie chooseTEPSerie(boolean isFirst) {
        TEPSerie p = null;
        this.patientDescriptTextField.setText("Ouverture de la série dynamique en cours...");
       
        JFileChooser chooser = this.dynSerieChooser;
        int returnVal = chooser.showOpenDialog(this);
        
        if (JFileChooser.APPROVE_OPTION == returnVal) {
            
            File choosenFile;
            choosenFile = chooser.getSelectedFile();
           
            // Un fois le dossier choisi
            
            if (choosenFile != null) {
                String patientDirPath;
                if (choosenFile.isDirectory()) {
                    patientDirPath = choosenFile.getAbsolutePath();
                }
                else {
                    patientDirPath = choosenFile.getParent();
                }
                
                
                //On peut créer un nouveau patient
                
               
		try {
			p = new TEPSerie(patientDirPath, true, isFirst);
                        JOptionPane.showMessageDialog(null, "L'acquisition a été ouverte avec succès\n\n"+p.toString(), "Info", JOptionPane.PLAIN_MESSAGE);
                        this.patientDescriptTextField.append(p.toString());
                        
                        
                        
                        System.out.println(p);
		} catch (NotDirectoryException | DicomFilesNotFoundException
                        | BadParametersException e) {
			// TODO Auto-generated catch block
                   
			JOptionPane.showMessageDialog(null, e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
                        this.patientDescriptTextField.setText("Erreur Lors de l'ouverture...");
                        
		} 
                
		
                
            }
            else {
                JOptionPane.showMessageDialog(null, "Problem accessing the file!", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
           
        } else {
            System.out.println("File access cancelled by user.");
        }
        return p;
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
      
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(main_window.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> {
            new main_window().setVisible(true);
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem closeAllMenu;
    private javax.swing.JMenuItem closeAllSeries;
    private javax.swing.JMenuItem closeMultiAcqMenu;
    private javax.swing.JDesktopPane desktop;
    private javax.swing.JMenuItem displayImagesMenu;
    private javax.swing.JFileChooser dynSerieChooser;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JPopupMenu.Separator jSeparator2;
    private javax.swing.JPopupMenu.Separator jSeparator3;
    private javax.swing.JMenuItem openMultiAcqMenu;
    private javax.swing.JMenuItem openTAPSerie;
    private javax.swing.JMenuItem openTEPSerie;
    private javax.swing.JFileChooser patientChooser;
    private javax.swing.JTextArea patientDescriptTextField;
    private javax.swing.JFileChooser statSerieChooser;
    // End of variables declaration//GEN-END:variables

    private synchronized TAPSerie chooseTAPSerie() {
        TAPSerie tapSerie = null;
        this.patientDescriptTextField.setText("Ouverture de la série statique en cours...");
       
        JFileChooser chooser = this.statSerieChooser;
        int returnVal = chooser.showOpenDialog(this);
        
        if (JFileChooser.APPROVE_OPTION == returnVal) {
            
            File choosenFile;
            choosenFile = chooser.getSelectedFile();
           
            // Un fois le dossier choisi
            
            if (choosenFile != null) {
                String patientDirPath;
                if (choosenFile.isDirectory()) {
                    patientDirPath = choosenFile.getAbsolutePath();
                }
                else {
                    patientDirPath = choosenFile.getParent();
                }
                
                
                //On peut créer un nouveau patient
                
               
		try {
			System.out.println("INIT SERIE TAP");
                        tapSerie = new TAPSerie(patientDirPath, true);
                        JOptionPane.showMessageDialog(null, "L'acquisition a été ouverte avec succès\n\n"+tapSerie.toString(), "Info", JOptionPane.PLAIN_MESSAGE);
                        this.patientDescriptTextField.append(tapSerie.toString());
                        
                        
                        
                        
                       
                        
                    
                        
		} catch (NotDirectoryException | DicomFilesNotFoundException
                        | BadParametersException e) {
			// TODO Auto-generated catch block
                   
			JOptionPane.showMessageDialog(null, e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
                        this.patientDescriptTextField.setText("Erreur Lors de l'ouverture...");
                        
		} catch (NoTAPSerieFoundException ex) {
                    Logger.getLogger(main_window.class.getName()).log(Level.SEVERE, null, ex);
                } 
                
		
                
            }
            else {
                JOptionPane.showMessageDialog(null, "Problem accessing the file!", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
           
        } else {
            System.out.println("File access cancelled by user.");
        }
        return tapSerie;
    }
}
