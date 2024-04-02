package voting;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class VotingSystemGUI extends JFrame {

    private CardLayout cardLayout;
    private JPanel cardPanel;
    private JTextField nameField;
    private JTextField ageField;
    private JTextField voterIdField;

    public VotingSystemGUI() {
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        JPanel loginPanel = createLoginPanel();
        JPanel partyPanel = createPartyPanel();

        cardPanel.add(loginPanel, "login");
        cardPanel.add(partyPanel, "party");

        add(cardPanel);

        setSize(600, 300);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private JPanel createLoginPanel() {
        JPanel loginPanel = new JPanel(new BorderLayout());
        JPanel inputPanel = new JPanel(new GridLayout(4, 2));
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

        nameField = new JTextField();
        ageField = new JTextField();
        voterIdField = new JTextField();
        JButton loginButton = new JButton("Login");
        JButton adminButton = new JButton("Admin Panel");

        inputPanel.add(new JLabel("Enter your Name:"));
        inputPanel.add(nameField);
        inputPanel.add(new JLabel("Enter your Age:"));
        inputPanel.add(ageField);
        inputPanel.add(new JLabel("Enter your Voter ID:"));
        inputPanel.add(voterIdField);

        buttonPanel.add(loginButton);
        buttonPanel.add(adminButton);

        loginPanel.add(inputPanel, BorderLayout.CENTER);
        loginPanel.add(buttonPanel, BorderLayout.SOUTH);

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String name = nameField.getText();
                String age = ageField.getText();
                String voterId = voterIdField.getText();

                // Validate age
                int ageInt = Integer.parseInt(age);
                if (ageInt >= 18) {
                    // Hash name, age, and voter ID
                    String hashedName = hashString(name);
                    String hashedAge = hashString(age);
                    String hashedVoterId = hashString(voterId);

                    // Navigate to Party Selection page after successful login
                    cardLayout.show(cardPanel, "party");
                } else {
                    JOptionPane.showMessageDialog(VotingSystemGUI.this, "You must be at least 18 years old to vote.", "Age Validation Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        adminButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Open Admin Panel
                new Admin().setVisible(true);
            }
        });

        return loginPanel;
    }

    private JPanel createPartyPanel() {
        JPanel partyPanel = new JPanel(new BorderLayout());
        JPanel buttonPanel = new JPanel(new GridLayout(4, 1));

        // Placeholder for party logos
        ImageIcon bjpLogo = new ImageIcon("bjp_logo.jpg");
        ImageIcon incLogo = new ImageIcon("inc_logo.jpg");
        ImageIcon aapLogo = new ImageIcon("aap_logo.jpg");

        JButton partyButton1 = new JButton("Bharatiya Janata Party (BJP)", bjpLogo);
        JButton partyButton2 = new JButton("Indian National Congress (INC)", incLogo);
        JButton partyButton3 = new JButton("Aam Aadmi Party (AAP)", aapLogo);
        JButton notaButton = new JButton("NOTA");

        buttonPanel.add(partyButton1);
        buttonPanel.add(partyButton2);
        buttonPanel.add(partyButton3);
        buttonPanel.add(notaButton);

        partyPanel.add(buttonPanel, BorderLayout.CENTER);

        partyButton1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	registerVote("BJP");
            }
        });

        partyButton2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                registerVote("INC");
            }
        });

        partyButton3.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                registerVote("AAP");
            }
        });

        notaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                registerVote("NOTA");
            }
        });

        return partyPanel;
    }

    private void registerVote(String party) {
        String name = nameField.getText();
        String age = ageField.getText();
        String voterId = voterIdField.getText();

        // Hash name, age, and voter ID
        String hashedName = hashString(name);
        String hashedAge = hashString(age);
        String hashedVoterId = hashString(voterId);

        // Register vote with hashed details
        VotingSystemDAO dao = new VotingSystemDAO();
        dao.insertVoterDetails(hashedName, hashedAge, hashedVoterId, party);
        dao.closeConnection();

        // Navigate back to login page after registering vote
        cardLayout.show(cardPanel, "login");
    }

    private String hashString(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes());
            StringBuilder hexString = new StringBuilder();

            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }

            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void  main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new VotingSystemGUI();
            }
        });
    }
}

