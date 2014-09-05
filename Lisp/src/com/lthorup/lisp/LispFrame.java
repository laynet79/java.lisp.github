package com.lthorup.lisp;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.JFileChooser;
import javax.swing.JToolBar;
import javax.swing.JSplitPane;
import javax.swing.JScrollPane;
import javax.swing.JEditorPane;
import javax.swing.JButton;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.awt.Color;

public class LispFrame extends JFrame {
	
	static final long serialVersionUID = 0;

	private Lisp lisp;
    private PrintHandler printer;
	private JPanel contentPane;
	private JEditorPane editor;
	private JEditorPane output;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					LispFrame frame = new LispFrame();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public LispFrame() {
		setTitle("Lisper");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 780, 705);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		JToolBar toolBar = new JToolBar();
		contentPane.add(toolBar, BorderLayout.NORTH);
		
		JButton btnEvaluate = new JButton("Evaluate");
		btnEvaluate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
		        String s = editor.getSelectedText();
		        if (s == null)
		            s = editor.getText();
		        output.setText("");
		        lisp.Interpret(s);
			}
		});
		toolBar.add(btnEvaluate);
		
		JButton btnLoad = new JButton("Load");
		btnLoad.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setFileFilter(new FileNameExtensionFilter("Prolog Files", new String[] {"pro"}));
				if (fileChooser.showOpenDialog(editor) == JFileChooser.APPROVE_OPTION) {
				  File file = fileChooser.getSelectedFile();
				  try {
					  editor.setText(readFile(file.getAbsolutePath()));
					  setTitle(file.getName());
				  }
				  catch(Exception error) {}
				  setTitle("Prolog: " + file.getName());
				}
			}
		});
		toolBar.add(btnLoad);
		
		JButton btnSave = new JButton("Save");
		btnSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setFileFilter(new FileNameExtensionFilter("Prolog Files", new String[] {"pro"}));
				if (fileChooser.showSaveDialog(editor) == JFileChooser.APPROVE_OPTION) {
				  File file = fileChooser.getSelectedFile();
				  try {
					  FileWriter f = new FileWriter(file.getAbsolutePath());
					  f.write(editor.getText());
					  f.close();
					  setTitle("Prolog: " + file.getName());
				  }
				  catch(Exception error) {}
				}
			}
		});
		toolBar.add(btnSave);
		
		JSplitPane splitPane = new JSplitPane();
		splitPane.setResizeWeight(0.75);
		splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		contentPane.add(splitPane, BorderLayout.CENTER);
		
		JScrollPane scrollPane = new JScrollPane();
		splitPane.setLeftComponent(scrollPane);
		
		editor = new JEditorPane();
		editor.setCaretColor(Color.WHITE);
		editor.setForeground(Color.GREEN);
		editor.setBackground(Color.BLACK);
		scrollPane.setViewportView(editor);
		
		JScrollPane scrollPane_1 = new JScrollPane();
		splitPane.setRightComponent(scrollPane_1);
		
		output = new JEditorPane();
		output.setEditable(false);
		output.setForeground(Color.GREEN);
		output.setBackground(Color.BLACK);
		scrollPane_1.setViewportView(output);
		
		lisp = new Lisp();
	    printer = new PrintHandler() { @Override public void print(String s) { printToOutput(s); }};
	    lisp.setPrinter(printer);
	}

    private void printToOutput(String s) {
        output.setText(output.getText() + s);
    }
    
	String readFile(String fileName) throws IOException {
	    BufferedReader br = new BufferedReader(new FileReader(fileName));
	    try {
	        StringBuilder sb = new StringBuilder();
	        String line = br.readLine();

	        while (line != null) {
	            sb.append(line);
	            sb.append("\n");
	            line = br.readLine();
	        }
	        return sb.toString();
	    } finally {
	        br.close();
	    }
	}
}
