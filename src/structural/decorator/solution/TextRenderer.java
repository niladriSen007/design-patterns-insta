package structural.decorator.solution;

interface TextView {
    void render();
}

class PlainTextView implements TextView {
    @Override
    public void render() {
        System.out.println("Rendering plain text");
    }
}

abstract class TextDecorator implements TextView {
    protected TextView textView;

    public TextDecorator(TextView textView) {
        this.textView = textView;
    }
}

class BoldDecorator extends TextDecorator {
    public BoldDecorator(TextView textView) {
        super(textView);
    }

    @Override
    public void render() {
        System.out.print("<b>");
        textView.render();
        System.out.print("</b>");
    }
}

class ItalicDecorator extends TextDecorator {
    public ItalicDecorator(TextView textView) {
        super(textView);
    }

    @Override
    public void render() {
        System.out.print("<i>");
        textView.render();
        System.out.print("</i>");
    }
}

class UnderlineDecorator extends TextDecorator {
    public UnderlineDecorator(TextView textView) {
        super(textView);
    }

    @Override
    public void render() {
        System.out.print("<u>");
        textView.render();
        System.out.print("</u>");
    }
}

public class TextRenderer {
    public static void main(String[] args) {
        TextView plainText = new PlainTextView();
        System.out.println("Plain Text:");
        plainText.render();

        System.out.println("\n\nBold Text:");
        TextView boldText = new BoldDecorator(new PlainTextView());
        boldText.render();

        System.out.println("\n\nItalic Text:");
        TextView italicText = new ItalicDecorator(new PlainTextView());
        italicText.render();

        System.out.println("\n\nUnderline Text:");
        TextView underlineText = new UnderlineDecorator(new PlainTextView());
        underlineText.render();

        System.out.println("\n\nBold and Italic Text:");
        TextView boldItalicText = new BoldDecorator(new ItalicDecorator(new PlainTextView()));
        boldItalicText.render();

        System.out.println("\n\nBold and Underline Text:");
        TextView boldUnderlineText = new BoldDecorator(new UnderlineDecorator(new PlainTextView()));
        boldUnderlineText.render();

        System.out.println("\n\nItalic and Underline Text:");
        TextView italicUnderlineText = new ItalicDecorator(new UnderlineDecorator(new PlainTextView()));
        italicUnderlineText.render();

        System.out.println("\n\nBold, Italic, and Underline Text:");
        TextView boldItalicUnderlineText = new BoldDecorator(
                new ItalicDecorator(new UnderlineDecorator(new PlainTextView())));
        boldItalicUnderlineText.render();
    }
}
