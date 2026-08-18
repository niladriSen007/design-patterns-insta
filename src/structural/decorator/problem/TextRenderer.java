package structural.decorator.problem;

interface TextView {
    void render();
}

class PlainTextView implements TextView {
    @Override
    public void render() {
        System.out.println("Rendering plain text");
    }
}

class BoldTextView implements TextView {
    @Override
    public void render() {
        System.out.print("<b>Rendering bold text: </b>");
    }
}

class ItalicTextView implements TextView {
    @Override
    public void render() {
        System.out.print("<i>Rendering italic text: </i>");
    }
}

class UnderlineTextView implements TextView {
    @Override
    public void render() {
        System.out.print("<u>Rendering underlined text: </u>");
    }
}

class BoldItalicTextView implements TextView {
    @Override
    public void render() {
        System.out.print("<b><i>Rendering bold and italic text: </i></b>");
    }
}

class BoldUnderlineTextView implements TextView {
    @Override
    public void render() {
        System.out.print("<b><u>Rendering bold and underlined text: </u></b>");
    }
}

class ItalicUnderlineTextView implements TextView {
    @Override
    public void render() {
        System.out.print("<i><u>Rendering italic and underlined text: </u></i>");
    }
}

class BoldItalicUnderlineTextView implements TextView {
    @Override
    public void render() {
        System.out.print("<b><i><u>Rendering bold, italic, and underlined text: </u></i></b>");
    }
}

public class TextRenderer {
    static void main() {
        TextView plainText = new PlainTextView();
        TextView boldText = new BoldTextView();
        TextView italicText = new ItalicTextView();
        TextView underlineText = new UnderlineTextView();
        TextView boldItalicText = new BoldItalicTextView();
        TextView boldUnderlineText = new BoldUnderlineTextView();
        TextView italicUnderlineText = new ItalicUnderlineTextView();
        TextView boldItalicUnderlineText = new BoldItalicUnderlineTextView();

        System.out.println("Rendering different text styles:");
        plainText.render();
        System.out.println();
        boldText.render();
        System.out.println();
        italicText.render();
        System.out.println();
        underlineText.render();
        System.out.println();
        boldItalicText.render();
        System.out.println();
        boldUnderlineText.render();
        System.out.println();
        italicUnderlineText.render();
        System.out.println();
        boldItalicUnderlineText.render();
    }
}
