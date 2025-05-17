import org.w3c.dom.*;
import javax.xml.parsers.*;
import java.io.File;

public class ParserDOM {
  public static void main(String[] args) {
    try {
      File archivoXML = new File("src/main/resources/boe.xml");

      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      factory.setValidating(true);

      DocumentBuilder builder = factory.newDocumentBuilder();
      Document document = builder.parse(archivoXML);

      Element root = document.getDocumentElement();
      System.out.println("Elemento raíz: " + root.getNodeName());

      System.out.println("Título: " + getTextoUnico(root, "TITULO"));

      Element fecha = (Element) root.getElementsByTagName("FECHA").item(0);
      String aaaa = getTextoUnico(fecha, "AAAA");
      String mm = getTextoUnico(fecha, "MM");
      String dd = getTextoUnico(fecha, "DD");
      System.out.println("Fecha: " + aaaa + "-" + mm + "-" + dd);

      NodeList secciones = document.getElementsByTagName("SECCION");
      for (int i = 0; i < secciones.getLength(); i++) {
        Element seccion = (Element) secciones.item(i);
        System.out.println("  Sección: " + getTextoUnico(seccion, "TIT-SECCION"));

        NodeList apartados = seccion.getElementsByTagName("APARTADO");
        for (int j = 0; j < apartados.getLength(); j++) {
          Element apartado = (Element) apartados.item(j);
          System.out.println("    Organismo: " + getTextoUnico(apartado, "ORGANISMO"));

          NodeList parrafos = apartado.getElementsByTagName("PARRAFO");
          for (int k = 0; k < parrafos.getLength(); k++) {
            Element parrafo = (Element) parrafos.item(k);
            Element texto = (Element) parrafo.getElementsByTagName("TEXTO").item(0);

            System.out.print("      Texto: ");
            NodeList hijosTexto = texto.getChildNodes();
            for (int l = 0; l < hijosTexto.getLength(); l++) {
              Node n = hijosTexto.item(l);
              System.out.print(n.getTextContent());
            }
            System.out.println();

            NodeList paginas = parrafo.getElementsByTagName("PAGINA");
            System.out.print("      Páginas: ");
            for (int p = 0; p < paginas.getLength(); p++) {
              System.out.print(paginas.item(p).getTextContent() + " ");
            }
            System.out.println();
          }
        }
      }

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private static String getTextoUnico(Element padre, String etiqueta) {
    NodeList nl = padre.getElementsByTagName(etiqueta);
    if (nl != null && nl.getLength() > 0) {
      return nl.item(0).getTextContent().trim();
    }
    return "";
  }
}
