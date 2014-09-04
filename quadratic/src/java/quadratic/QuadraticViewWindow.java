package quadratic;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.wb.swt.SWTResourceManager;

import zetes.wings.base.ViewWindowBase;

import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;

import quadratic.QuadraticDocument.State;

public class QuadraticViewWindow extends ViewWindowBase<QuadraticDocument>
{
	private static final String MESSAGE_PUSH_SOLVE_BUTTON = "Push the solve button to calculate the roots";
	private static final String MESSAGE_INVALID_INPUT = "Invalid input"; 
	private static final String MESSAGE_UNSOLVABLE = "This equation is unsolvable on real field"; 
	private static final String MESSAGE_TOO_MANY_ROOTS = "This equation has too many roots"; 
	
	private Button buttonSolve;
	private Text text_a;
	private Text text_b;
	private Text text_c;
	private Label label_status;
	private boolean selfModifying = false;
	
	private void showResult() {
		QuadraticDocument doc = getDocument();
		if (doc.getState() == State.validRoots) {
			label_status.setText("x1 = " + doc.getX1() + ", x2 = " + doc.getX2());
		} else if (doc.getState() == State.unsolvable) {
			label_status.setText(MESSAGE_UNSOLVABLE);
		} else if (doc.getState() == State.unsolved) {
			label_status.setText(MESSAGE_PUSH_SOLVE_BUTTON);
		} else if (doc.getState() == State.tooManyRoots) {
			label_status.setText(MESSAGE_TOO_MANY_ROOTS);
		} else {
			throw new RuntimeException("Invalid case");
		}
	}
	
	private void setDataAndValidate() {
		if (!selfModifying) {
			try {
				selfModifying = true;
				getDocument().setA(Double.parseDouble(text_a.getText()));
				getDocument().setB(Double.parseDouble(text_b.getText()));
				getDocument().setC(Double.parseDouble(text_c.getText()));
				label_status.setText(MESSAGE_PUSH_SOLVE_BUTTON);
				buttonSolve.setEnabled(true);
				
			} catch (NumberFormatException e) {
				label_status.setText(MESSAGE_INVALID_INPUT);
				buttonSolve.setEnabled(false);
			} finally {
				selfModifying = false;
			}
		}
	}
	
	private ModifyListener abcModifyListener = new ModifyListener() {
		public void modifyText(ModifyEvent arg0) {
			setDataAndValidate();
		}
	};
	
	/**
	 * Create contents of the window.
	 * 
	 * @wbp.parser.entryPoint
	 */
	@Override
	protected Shell constructShell()
	{
		Shell shell = new Shell(SWT.TITLE | SWT.CLOSE | SWT.MIN | SWT.MAX | SWT.RESIZE | SWT.BORDER | SWT.DOUBLE_BUFFERED);
		shell.setSize(339, 177);

		shell.setMinimumSize(new Point(250, 200));

		shell.setImages(new Image[] { 
				SWTResourceManager.getImage(QuadraticViewWindow.class, "/quadratic/quadratic512.png"),		// Necessary in OS X
				SWTResourceManager.getImage(QuadraticViewWindow.class, "/quadratic/quadratic64.png"),		// Necessary in Windows (for Alt-Tab)
				SWTResourceManager.getImage(QuadraticViewWindow.class, "/quadratic/quadratic16.png")		// Necessary in Windows (for taskbar)
		});
		shell.setLayout(new GridLayout(6, false));
		
		Label lblEnterQuadraticEquation = new Label(shell, SWT.NONE);
		lblEnterQuadraticEquation.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 6, 1));
		lblEnterQuadraticEquation.setText("Enter quadratic equation");
		
		text_a = new Text(shell, SWT.BORDER);
		text_a.addModifyListener(abcModifyListener);
		text_a.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label lblNewLabel = new Label(shell, SWT.NONE);
		lblNewLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblNewLabel.setText(" * x^2 + ");
		
		text_b = new Text(shell, SWT.BORDER);
		text_b.addModifyListener(abcModifyListener);
		text_b.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label lblNewLabel_1 = new Label(shell, SWT.NONE);
		lblNewLabel_1.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblNewLabel_1.setText(" * x + ");
		
		text_c = new Text(shell, SWT.BORDER);
		text_c.addModifyListener(abcModifyListener);
		text_c.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label label = new Label(shell, SWT.NONE);
		label.setText("= 0");
		
		buttonSolve = new Button(shell, SWT.NONE);
		buttonSolve.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 6, 1));
		buttonSolve.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				getDocument().solve();
				showResult();
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) { }
		});
		buttonSolve.setText("Solve");
		
		label_status = new Label(shell, SWT.NONE);
		label_status.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 6, 1));
		label_status.setText("Push the solve button to calculate the roots");
	
			
		return shell;
	}

	@Override
	public void setDocument(QuadraticDocument document)
	{
		super.setDocument(document);
		if (document == null) {
			text_a.setEnabled(false);
			text_b.setEnabled(false);
			text_c.setEnabled(false);
			buttonSolve.setEnabled(false);
			label_status.setText("");
		} else {
			if (!selfModifying) {
				selfModifying = true;
				try {
					text_a.setText(((Double)document.getA()).toString());
					text_b.setText(((Double)document.getB()).toString());
					text_c.setText(((Double)document.getC()).toString());
					
					text_a.setEnabled(true);
					text_b.setEnabled(true);
					text_c.setEnabled(true);
					buttonSolve.setEnabled(true);
					label_status.setText(MESSAGE_PUSH_SOLVE_BUTTON);
				} finally {
					selfModifying = false;
				}
			}
		}
		
		getShell().forceActive();
	}
	
	@Override
	public boolean supportsFullscreen()
	{
		return true;
	}

	@Override
	public boolean supportsMaximizing()
	{
		return true;
	}
}
