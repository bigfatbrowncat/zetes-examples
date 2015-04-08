package htmlview;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.events.ShellListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Shell;

import zetes.wings.base.ViewWindowBase;
import zetes.wings.litehtml.swt.LiteHTMLView;

public class HTMLViewWindow extends ViewWindowBase<HTMLDocument> {

	private LiteHTMLView liteHTMLView;
	private ScrolledComposite scrolledComposite;
	
	private void refreshContent() {
		Point minPageSize = liteHTMLView.computeSize(scrolledComposite.getClientArea().width, SWT.DEFAULT);
		if (minPageSize.y < scrolledComposite.getClientArea().height) {
			minPageSize.y = scrolledComposite.getClientArea().height;
		}
		liteHTMLView.setSize(minPageSize);
	}
	
	@Override
	public boolean supportsFullscreen() {
		return true;
	}


	@Override
	public boolean supportsMaximizing() {
		return true;
	}

	@Override
	public void setDocument(HTMLDocument document) {
		super.setDocument(document);
		liteHTMLView.setContent(document.getHTMLText(), "html { display: block; position: relative; } "
				+ "title { display: none; } "
				+ "body { display: block; margin:12px; background-color: white; } "
				+ "p { display: block; margin-top: 1em; margin-bottom: 1em; } "
				+ "h1 { font-size: 150%; }"

				+ "ul, menu, dir {"
				+ 	"display: block;"
				+ 	"list-style-type: disc;"
				+ 	"margin-top: 1em;"
				+ 	"margin-bottom: 1em;"
				+ 	"margin-left: 0;"
				+ 	"margin-right: 0;"
				+ 	"padding-left: 40px"
				+ "}"

				+ "ol {"
				+ 	"display: block;"
				+ 	"list-style-type: decimal;"
				+ 	"margin-top: 1em;"
				+ 	"margin-bottom: 1em;"
				+ 	"margin-left: 0;"
				+ 	"margin-right: 0;"
				+ 	"padding-left: 40px"
				+ "}"

				+ "li {"
				+ 	"display: list-item;"
				+ "}"

				+ "b {"
				+ 	"font-weight: bold;"
				+ "}"

				+ "i {"
				+ 	"font-style: italic;"
				+ "}");
		
		refreshContent();
	}

	@Override
	protected Shell constructShell() {
		Shell shell = new Shell(SWT.MIN | SWT.MAX | SWT.CLOSE | SWT.RESIZE | SWT.DOUBLE_BUFFERED);
		shell.setSize(450, 300);
		shell.setText("LiteHTML Demo");
		shell.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		scrolledComposite = new ScrolledComposite(shell, SWT.V_SCROLL);
		scrolledComposite.setExpandHorizontal(true);
		
		liteHTMLView = new LiteHTMLView(scrolledComposite, SWT.DOUBLE_BUFFERED);
		scrolledComposite.setContent(liteHTMLView);
		
		ControlListener scrollControlListener = new ControlListener() {
			
			@Override
			public void controlResized(ControlEvent arg0) {
				refreshContent();
			}
			
			@Override public void controlMoved(ControlEvent arg0) { }
		};
		
		scrolledComposite.addControlListener(scrollControlListener);
		return shell;
	}
}
