/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.net.*;
import java.io.*;

/**
 *
 * @author Neeraj
 */
public class MainFrame extends javax.swing.JFrame implements Runnable {

    public MainFrame() {
        super("GUI Chat Application");
        initComponents();
        this.setVisible(true);
        this.sendButton.setEnabled(false);
        this.isClosed = false;
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        receiveTextArea = new javax.swing.JTextArea();
        jScrollPane2 = new javax.swing.JScrollPane();
        sendTextArea = new javax.swing.JTextArea();
        sendButton = new javax.swing.JButton();
        usernameTextArea = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        enableServer = new javax.swing.JRadioButton();
        jSeparator1 = new javax.swing.JSeparator();
        jSeparator2 = new javax.swing.JSeparator();
        serverIpAddress = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        connectButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        receiveTextArea.setColumns(20);
        receiveTextArea.setRows(5);
        jScrollPane1.setViewportView(receiveTextArea);

        getContentPane().add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 11, 507, 243));

        sendTextArea.setColumns(20);
        sendTextArea.setRows(5);
        jScrollPane2.setViewportView(sendTextArea);

        getContentPane().add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 265, 507, 108));

        sendButton.setText("Send");
        sendButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sendButtonActionPerformed(evt);
            }
        });
        getContentPane().add(sendButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(208, 436, 106, 34));
        getContentPane().add(usernameTextArea, new org.netbeans.lib.awtextra.AbsoluteConstraints(535, 47, 200, -1));

        jLabel1.setText("                    Username:");
        getContentPane().add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(535, 20, 200, 21));

        enableServer.setText("Enable Server");
        enableServer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                enableServerActionPerformed(evt);
            }
        });
        getContentPane().add(enableServer, new org.netbeans.lib.awtextra.AbsoluteConstraints(535, 96, -1, -1));
        getContentPane().add(jSeparator1, new org.netbeans.lib.awtextra.AbsoluteConstraints(535, 79, 200, 10));
        getContentPane().add(jSeparator2, new org.netbeans.lib.awtextra.AbsoluteConstraints(535, 126, 200, 10));

        serverIpAddress.setText("192.168.0.100");
        getContentPane().add(serverIpAddress, new org.netbeans.lib.awtextra.AbsoluteConstraints(535, 172, 200, 28));

        jLabel2.setText("Server IP Address:");
        getContentPane().add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(535, 147, 200, -1));

        connectButton.setText("Connect");
        connectButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                connectButtonActionPerformed(evt);
            }
        });
        getContentPane().add(connectButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(535, 218, 200, -1));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void enableServerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_enableServerActionPerformed
        // TODO add your handling code here:
        if (enableServer.isSelected()) {
            receiveTextArea.setText("Waiting for remote user...");
            serverIpAddress.setEditable(false);
            connectButton.setEnabled(false);
            this.isServer = true;
        } else {
            receiveTextArea.setText("");
            serverIpAddress.setEditable(true);
            connectButton.setEnabled(true);
            this.isServer = false;
        }
    }//GEN-LAST:event_enableServerActionPerformed

    private void connectButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_connectButtonActionPerformed
        // TODO add your handling code here:
        connectToServer = true;
    }//GEN-LAST:event_connectButtonActionPerformed

    private void sendButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sendButtonActionPerformed
        // TODO add your handling code here:
        String message = sendTextArea.getText();
        message = localUsername + ": " + message;
        try {
            out.write(message);
            out.newLine();
            out.flush();
            sendTextArea.setText("");
            String allText = receiveTextArea.getText();
            allText = allText + "\n" + message;
            receiveTextArea.setText(allText);
        } catch (Exception e) {
            disconnect();
        }
    }//GEN-LAST:event_sendButtonActionPerformed

    public void run() {
        while (!isClosed) {
            sleep(100);
            if (isServer) {
                initializeServerMode();
            }
            if (connectToServer) {
                initializeClientMode();
            }
        }
    }

    private void initializeServerMode() {
        try {
            server = new ServerSocket(portNumber);
            client = server.accept();
            out = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
            in = new BufferedReader(new InputStreamReader(client.getInputStream()));

            remoteUsername = in.readLine();

            localUsername = usernameTextArea.getText();
            localUsername = localUsername.trim();
            if (localUsername.length() == 0) {
                localUsername = "Server";
            }
            out.write(localUsername);
            out.newLine();
            out.flush();
            receiveTextArea.setText("User: " + remoteUsername + " is connected! You can start the chat conversation");
            sendButton.setEnabled(true);
            enableServer.setEnabled(false);

            readIncomingMessages();
        } catch (BindException e1) {
            receiveTextArea.setText("Port " + portNumber + " is used by another application!");
        } catch (Exception e) {
            System.out.println("server error!" + e.toString());
            disconnect();
        }
    }

    private void initializeClientMode() {
        try {
            String ip = serverIpAddress.getText();
            client = new Socket(ip, portNumber);
            out = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
            in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            localUsername = usernameTextArea.getText();
            if (localUsername.length() == 0) {
                localUsername = "Client";
            }

            localUsername = localUsername.trim();
            out.write(localUsername);
            out.newLine();
            out.flush();

            remoteUsername = in.readLine();

            receiveTextArea.setText("User: " + remoteUsername + " is connected! You can start the chat conversation");
            sendButton.setEnabled(true);
            enableServer.setEnabled(false);
            connectButton.setEnabled(false);

            readIncomingMessages();
        } catch (UnknownHostException e2) {
            receiveTextArea.setText("the IP address of the host could not be determined!");
        } catch (ConnectException e1) {
            receiveTextArea.setText("Server is unreachable!");
        } catch (Exception e) {
            System.out.println("client error!" + e.toString());
            disconnect();
        }
    }

    public void readIncomingMessages() {
        try {
            while (true) {
                String message = in.readLine();
                String allText = receiveTextArea.getText();
                allText = allText + "\n" + message;
                receiveTextArea.setText(allText);
            }
        } catch (Exception e) {
            System.out.println(e.toString() + "in readIncomingMessages");
            disconnect();
        }
    }

    public void disconnect() {
        try {
            String allText = receiveTextArea.getText();
            allText = allText + "\n" + this.remoteUsername + " has been disconected! \nThe connection is closed."; //info message
            receiveTextArea.setText(allText);
            if (isServer) {

                if (in != null) {
                    in.close();
                }
                if (out != null) {
                    out.close();
                }
                if (client != null) {
                    client.close();
                }
                if (server != null) {
                    server.close();
                }
                enableServer.setEnabled(true);
                enableServer.setSelected(false);
                serverIpAddress.setEditable(true);
                connectButton.setEnabled(true);
                sendButton.setEnabled(false);
                isServer = false;
            }
            if (connectToServer) {
                if (in != null) {
                    in.close();
                }
                if (out != null) {
                    out.close();
                }
                if (client != null) {
                    client.close();
                }
                enableServer.setEnabled(true);
                enableServer.setSelected(false);
                serverIpAddress.setEditable(true);
                connectButton.setEnabled(true);
                sendButton.setEnabled(false);
                connectToServer = false;
            }
        } catch (Exception e) {
            System.out.println("in disconnect" + e.toString());
        }
    }

    private void sleep(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (Exception e) {
            System.exit(1);
        }
    }

    private boolean isServer = false;
    private boolean connectToServer = false;
    private ServerSocket server;
    private Socket client;
    private final int portNumber = 9090;
    private BufferedWriter out;
    private BufferedReader in;
    private String localUsername;
    private String remoteUsername;
    private boolean isClosed = true;


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton connectButton;
    private javax.swing.JRadioButton enableServer;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JTextArea receiveTextArea;
    private javax.swing.JButton sendButton;
    private javax.swing.JTextArea sendTextArea;
    private javax.swing.JTextField serverIpAddress;
    private javax.swing.JTextField usernameTextArea;
    // End of variables declaration//GEN-END:variables
}
