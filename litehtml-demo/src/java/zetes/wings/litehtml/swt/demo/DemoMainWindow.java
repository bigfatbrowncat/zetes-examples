package zetes.wings.litehtml.swt.demo;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.custom.ScrolledComposite;

import zetes.wings.litehtml.swt.LiteHTMLView;

public class DemoMainWindow {

	protected Shell shell;
	
	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			DemoMainWindow window = new DemoMainWindow();
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Open the window.
	 */
	public void open() {
		Display display = Display.getDefault();
		createContents();
		shell.open();
		shell.layout();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		shell = new Shell(SWT.MIN | SWT.MAX | SWT.RESIZE | SWT.DOUBLE_BUFFERED);
		shell.setSize(450, 300);
		shell.setText("LiteHTML Demo");
		shell.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		final ScrolledComposite scrolledComposite = new ScrolledComposite(shell, SWT.V_SCROLL);
		scrolledComposite.setExpandHorizontal(true);
		
		final LiteHTMLView liteHTMLView = new LiteHTMLView(scrolledComposite, SWT.DOUBLE_BUFFERED);
		scrolledComposite.setContent(liteHTMLView);
		
		liteHTMLView.setContent("<html>"
				+ "<head><style>h1 { font-weight: bold; color: #88AAFF; }</style></head>"
				+ "<body>"
				+ "<h1>"
				+ 	"Lorem ipsum"
				+ "</h1>"
				+ "<p>Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet.</p>"   
				+ "<p>Duis autem vel eum iriure dolor in hendrerit in vulputate velit esse molestie consequat, vel illum dolore eu feugiat nulla facilisis at vero eros et accumsan et iusto odio dignissim qui blandit praesent luptatum zzril delenit augue duis dolore te feugait nulla facilisi. Lorem ipsum dolor sit amet, consectetuer adipiscing elit, sed diam nonummy nibh euismod tincidunt ut laoreet dolore magna aliquam erat volutpat.</p>"   
				+ "<p>Ut wisi enim ad minim veniam, quis nostrud exerci tation ullamcorper suscipit lobortis nisl ut aliquip ex ea commodo consequat. Duis autem vel eum iriure dolor in hendrerit in vulputate velit esse molestie consequat, vel illum dolore eu feugiat nulla facilisis at vero eros et accumsan et iusto odio dignissim qui blandit praesent luptatum zzril delenit augue duis dolore te feugait nulla facilisi.</p>"   
				+ "<p>Nam liber tempor cum soluta nobis eleifend option congue nihil imperdiet doming id quod mazim placerat facer</p>"
				+ "</body>"
				+ "</html>", "html { display: block; position: relative; } "
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
		
		

		ControlListener scrollControlListener = new ControlListener() {
			
			@Override
			public void controlResized(ControlEvent arg0) {
				Point minPageSize = liteHTMLView.computeSize(scrolledComposite.getClientArea().width, SWT.DEFAULT);
				if (minPageSize.y < scrolledComposite.getClientArea().height) {
					minPageSize.y = scrolledComposite.getClientArea().height;
				}
				liteHTMLView.setSize(minPageSize);
			}
			
			@Override
			public void controlMoved(ControlEvent arg0) {
				// TODO Auto-generated method stub
				
			}
		};
		
		scrolledComposite.addControlListener(scrollControlListener);
		scrollControlListener.controlResized(null);
		
	}
}
