package parrot.server.templates;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TemplateParser {
	private static class SyntaxError extends Error {
		private static final long serialVersionUID = 5738535723335945910L;
		private SyntaxError(int line, int col, String message) {
			super("Parsing error at line " + line + ", column " + col + ". " + message);
		}
	}
	
	public static class ParsedTemplate {
		private static class ProcessingError extends Error {
			private static final long serialVersionUID = 4575759064715513267L;
			private ProcessingError(String message) {
				super(message);
			}
		}
		
		public static class Context implements Cloneable {
			private HashMap<String, Object> variables = new HashMap<>();
			private LoopHandler loopHandler = new LoopHandler() {
				@Override
				public boolean next(Context context, String argument) {
					throw new ProcessingError("No loop handler connected");
				}
			};
			private IfHandler ifHandler = new IfHandler() {
				@Override
				public boolean choose(Context context, String argument) {
					throw new ProcessingError("No if handler connected");
				}
			};
			
			public void setVariableValue(String name, Object value) {
				variables.put(name, value);
			}
			public Object getVariableValue(String name) {
				return variables.get(name);
			}
			
			@SuppressWarnings("unchecked")
			@Override
			public Context clone() {
				Context c = new Context();
				c.variables = (HashMap<String, Object>) variables.clone();
				return c;
			}
			
			public void setIfHandler(IfHandler ifHandler) {
				this.ifHandler = ifHandler;
			}
			
			public IfHandler getIfHandler() {
				return ifHandler;
			}
			
			public void setLoopHandler(LoopHandler loopHandler) {
				this.loopHandler = loopHandler;
			}
			
			public LoopHandler getLoopHandler() {
				return loopHandler;
			}
		}
		
		public interface Function {
			String getArgument();
		}
		
		public interface LoopHandler {
			boolean next(Context context, String argument);
		}
		public interface IfHandler {
			boolean choose(Context context, String argument);
		}
		
		private interface Node {
			String process(Context context);
		}
		
		private class TextNode implements Node {
			private String text;
			public TextNode(String text) {
				this.text = text;
			}
			
			@Override
			public String process(Context context) {
				return text;
			}
		}
		
		private class OutNode implements Node, Function {
			private String argument;
			public OutNode(String argument) {
				this.argument = argument;
			}
			
			@Override
			public String process(Context context) {
				// TODO Here we should escape some characters
				Object varVal = context.getVariableValue(argument);
				if (varVal != null) {
					return varVal.toString();
				} else {
					throw new ProcessingError("Variable \"" + argument + "\" hasn't been set");
				}
			}

			@Override
			public String getArgument() {
				return argument;
			}
		}
		
		private class CompoundNode implements Node {
			private LinkedList<Node> subNodes = new LinkedList<Node>();
			public void addSubNode(Node node) {
				subNodes.add(node);
			}
			
			@Override
			public String process(Context context) {
				StringBuilder builder = new StringBuilder();
				for (Node n : subNodes) {
					builder.append(n.process(context));
				}
				return builder.toString();
			}
			
			public LinkedList<Node> getSubNodes() {
				return subNodes;
			}
		}
		
		private class LoopNode extends CompoundNode implements Function {
			private String argument;
			public LoopNode(String argument) {
				this.argument = argument;
			}
			
			@Override
			public String process(Context context) {
				StringBuilder builder = new StringBuilder();
				while (context.loopHandler.next(context, argument)) {
					builder.append(super.process(context));
				}
				return builder.toString();
			}
			
			@Override
			public String getArgument() {
				return argument;
			}
		}

		private class IfNode extends CompoundNode implements Function {
			private String argument;
			private int elsePosition;
			private boolean elsePosIsSet;
			
			public IfNode(String argument) {
				this.argument = argument;
			}
			
			@Override
			public void addSubNode(Node node) {
				super.addSubNode(node);
			}
			
			@Override
			public String process(Context context) {
				StringBuilder builder = new StringBuilder();
				
				if (context.ifHandler.choose(context, argument)) {
					// Positive
					for (int i = 0; i < elsePosition; i++) {
						builder.append(getSubNodes().get(i).process(context));
					}
				} else {
					// Negative
					for (int i = elsePosition; i < getSubNodes().size(); i++) {
						builder.append(getSubNodes().get(i).process(context));
					}
				}
				return builder.toString();
			}
			
			@Override
			public String getArgument() {
				return argument;
			}
			
			public void setElsePositionIfNotSet() {
				if (!elsePosIsSet) {
					elsePosition = getSubNodes().size();
					elsePosIsSet = true;
				}
			}
		}
		
		
		private CompoundNode rootNode = new CompoundNode();
		
		public void process(Context context, PrintStream target) throws IOException {
			target.println(rootNode.process(context));
		}
	}
	
	private TemplateParser() {
	}
	
	public static ParsedTemplate parse(InputStream inputStream) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
		ParsedTemplate target = new ParsedTemplate();
		LinkedList<ParsedTemplate.CompoundNode> nodesStack = new LinkedList<ParsedTemplate.CompoundNode>();
		nodesStack.add(target.rootNode);
		Pattern pattern = Pattern.compile("@([a-zA-Z0-9_:]*)(\\{[a-zA-Z0-9_]*\\})?");
		int lineIndex = 0;
		String line;
		while ((line = reader.readLine()) != null) {
			line += '\n';
			lineIndex ++;
			Matcher matcher = pattern.matcher(line);
			int nodeStart = 0;
			while (matcher.find()) {
				String funcGroup = matcher.group(1);
				String g2 = matcher.group(2);
				String argGroup = (g2 != null ? g2.substring(1, g2.length() - 1) : null);
				//System.out.println("start: " + matcher.start() + ", end: " + matcher.end() + ", @" + funcGroup + "{" + argGroup + "}");
				if (nodeStart < matcher.start()) {
					nodesStack.get(nodesStack.size() - 1).addSubNode(target.new TextNode(line.substring(nodeStart, matcher.start())));
					//System.out.println("Added TextNode: \"" + line.substring(nodeStart, matcher.start()) + "\"");
				}
				
				if (funcGroup.equals("")) {
					nodesStack.getLast().addSubNode(target.new OutNode(argGroup));
					//System.out.println("Added OutNode: \"" + argGroup + "\"");
				} else if (funcGroup.equals("loop")) {
					ParsedTemplate.CompoundNode loopNode = target.new LoopNode(argGroup);
					nodesStack.getLast().addSubNode(loopNode);
					nodesStack.add(loopNode);
					//System.out.println("Added For: \"" + argGroup + "\"");
				} else if (funcGroup.equals("if")) {
					ParsedTemplate.CompoundNode ifNode = target.new IfNode(argGroup);
					nodesStack.getLast().addSubNode(ifNode);
					nodesStack.add(ifNode);
				} else if (funcGroup.equals("else")) {
					if (argGroup != null) {
						throw new SyntaxError(lineIndex, matcher.start() + 1, "@else shouldn't have any arguments");
					} else if (nodesStack.getLast() instanceof ParsedTemplate.IfNode) {
						ParsedTemplate.IfNode ifNode = (ParsedTemplate.IfNode)nodesStack.getLast();
						ifNode.setElsePositionIfNotSet();
					} else {
						throw new SyntaxError(lineIndex, matcher.start() + 1, "Unexpected @else");
					}
				} else if (funcGroup.equals("end")) {
					if (argGroup != null) {
						throw new SyntaxError(lineIndex, matcher.start() + 1, "@end shouldn't have any arguments");
					} else if (nodesStack.getLast() instanceof ParsedTemplate.LoopNode) {
						// Nothing special
					} else if (nodesStack.getLast() instanceof ParsedTemplate.IfNode) {
						((ParsedTemplate.IfNode)nodesStack.getLast()).setElsePositionIfNotSet();
					} else {
						throw new SyntaxError(lineIndex, matcher.start() + 1, "Unexpected @end");
					}
					nodesStack.removeLast();
				} else {
					throw new SyntaxError(lineIndex, matcher.start() + 1, "Unknown command: " + funcGroup);
				}
				nodeStart = matcher.end();
			}
			if (nodeStart < line.length()) {
				nodesStack.getLast().addSubNode(target.new TextNode(line.substring(nodeStart)));
				//System.out.println("Added TextNode: \"" + line.substring(nodeStart) + "\"");
			}
		}
		if (nodesStack.size() > 1) {
			throw new SyntaxError(lineIndex, 1, "Expected @end");
		}
		return target;
	}
}
