/*
 * This Code belongs to his creator Cyriac Azefack and the lab QuantIF of the "Centre Henri Becquerel de Rouen"
 *   * 
 */
package QuantIF_Project.process;

import java.awt.Color;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 *
 * @author Cyriac
 */
public class InputDataField extends JTextField {
    
    /**
     * Pattern à respecter 
     */
    private Pattern pattern; 
    
    /**
     * Taille max du texte
     */
    private int textSize;
    
    /**
     * Vaut <b>true</b> si la valeur présente dans la zone est valide
     */
    private boolean checked;
    
    /**
     * JTextField pour gérer les valeurs rentrées par l'utilisateur
     * @param size
     * @param regex 
     */
    public InputDataField(int size, String regex) {
        super(size);
        checked = false;
        this.textSize = size;
        pattern = Pattern.compile(regex);
        
        //On gère le changement de couleur
        this.getDocument().addDocumentListener(new DocumentListener() {

            @Override
            public void insertUpdate(DocumentEvent e) {
                update();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                update();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                update();
            }
        });
    }
    
    /**
     * On mets à jour la bordure de la zone de saisie
     */
    private void update() {
        String s = this.getText();
        Matcher m = pattern.matcher(s);
        
        if (m.matches()) {
            this.setBorder(new javax.swing.border.LineBorder(Color.green, 1, true));
            this.checked = true;
        }
        else {
            this.setBorder(new javax.swing.border.LineBorder(Color.red, 1, true));
            this.checked = false;
        }
        
        
    }
    
    /**
     * La texte présent dans le champ est valide
     * @return 
     */
    public boolean checked() {
        return checked;
    }
}
