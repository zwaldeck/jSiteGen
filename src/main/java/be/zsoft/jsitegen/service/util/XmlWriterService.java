package be.zsoft.jsitegen.service.util;

import be.zsoft.jsitegen.service.ShellColorHelper;
import lombok.RequiredArgsConstructor;
import org.jline.terminal.Terminal;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.nio.file.Path;

@RequiredArgsConstructor

@Service
public class XmlWriterService {

    private final Terminal terminal;
    private final ShellColorHelper shellColorHelper;

    public void saveDocument(Document doc, Path outputPath) throws TransformerException {
        Path savePath = Path.of(outputPath.toString(), "sitemap.xml");
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(savePath.toFile());
        transformer.transform(source, result);

        terminal.writer().println(shellColorHelper.getSuccessMessage("Saved sitemap: %s".formatted(savePath)));
        terminal.flush();
    }
}
