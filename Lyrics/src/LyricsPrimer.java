import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoundedRangeModel;
import javax.swing.DefaultBoundedRangeModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.text.DefaultCaret;

public class LyricsPrimer {

	public static void main(String[] args) {
		Traitement.init();
		new LyricsPrimer();
	}


	public LyricsPrimer() {
		JFrame window = new JFrame();
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setTitle("LyricsPrimer v2.9");
		window.setResizable(true);

		JPanel sourceTextPanel = new JPanel();
		sourceTextPanel.setBorder(BorderFactory.createTitledBorder("In"));
		final JTextPane sourceText = new JTextPane();
		sourceText.setPreferredSize(new Dimension(600, 800));
		((DefaultCaret) sourceText.getCaret()).setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		JScrollPane sourceSP = new JScrollPane(sourceText, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		sourceTextPanel.add(sourceSP);

		JPanel destTextPanel = new JPanel();
		destTextPanel.setBorder(BorderFactory.createTitledBorder("Out"));
		final JTextPane destText = new JTextPane();
		destText.setEditable(false);
		destText.setPreferredSize(new Dimension(600, 800));
		((DefaultCaret) destText.getCaret()).setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		JScrollPane destSP = new JScrollPane(destText, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		destTextPanel.add(destSP);

		// Tenta de sync les deux mais �a fait juste des probl�mes
		BoundedRangeModel unifiedModel = new DefaultBoundedRangeModel();
		unifiedModel.setRangeProperties(sourceSP.getVerticalScrollBar().getModel().getValue(),
				sourceSP.getVerticalScrollBar().getModel().getExtent(),
				sourceSP.getVerticalScrollBar().getModel().getMinimum(),
				sourceSP.getVerticalScrollBar().getModel().getMaximum(),
				sourceSP.getVerticalScrollBar().getModel().getValueIsAdjusting());
		destSP.getVerticalScrollBar().setModel(unifiedModel);
		// sourceSP.getVerticalScrollBar().setModel(unifiedModel);

		JPanel buttonPanel = new JPanel();
		JButton theButton = new JButton("Go!");
		theButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				destText.setText(Traitement.traiter((String) sourceText.getText()));
				destText.setCaretPosition(0);
				sourceSP.getVerticalScrollBar().setModel(unifiedModel);
			}
		});

		JButton theSecondButton = new JButton("Yamete ça va trop vite !");
		theSecondButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				destText.setText(Traitement.optimize((String) destText.getText()));
				destText.setCaretPosition(0);
//            	sourceSP.getVerticalScrollBar().setModel(unifiedModel);
			}
		});

		JButton theOtherSecondButton = new JButton("Extract chorus!");
		theOtherSecondButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				destText.setText(Traitement.splitThatShit((String) destText.getText()));
				destText.setCaretPosition(0);
//            	sourceSP.getVerticalScrollBar().setModel(unifiedModel);
			}
		});

		buttonPanel.add(theButton);
		buttonPanel.add(theSecondButton);
		buttonPanel.add(theOtherSecondButton);




        window.add((Component)sourceTextPanel, "West");
        window.add((Component)destTextPanel, "East");
        window.add((Component)buttonPanel, "South");

		window.pack();
		
        window.setMinimumSize(window.getSize());
		window.setLocationRelativeTo(null);
		window.setVisible(true);

	}

}