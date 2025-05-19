import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;

public class Main {
  public static void main(String[] args) {
    try {
      String xml = """
          <?xml version="1.0" encoding="ISO-8859-1"?>
          <!DOCTYPE BOE [
            <!-- Aquí comienza el DTD -->
            <!ELEMENT BOE (TITULO, FECHA, SUMARIO)>
            <!ELEMENT TITULO (#PCDATA)>
            <!ELEMENT FECHA (AAAA, MM, DD)>
            <!ELEMENT AAAA (#PCDATA)>
            <!ELEMENT MM (#PCDATA)>
            <!ELEMENT DD (#PCDATA)>
            <!ELEMENT SUMARIO (SECCION+)>
            <!ELEMENT SECCION (TIT-SECCION, APARTADO+)>
            <!ELEMENT TIT-SECCION (#PCDATA)>
            <!ELEMENT APARTADO (ORGANISMO, PARRAFO+)>
            <!ELEMENT ORGANISMO (#PCDATA)>
            <!ELEMENT PARRAFO (TEXTO, PAGINA+)>
            <!ELEMENT TEXTO (#PCDATA | LEY | ORDEN | RD | RES)*> <!-- o tiene una ley,orden,real decreto,resolucion o simplemente texto-->
            <!ELEMENT LEY (#PCDATA)>
            <!ELEMENT ORDEN (#PCDATA)>
            <!ELEMENT RD (#PCDATA)>
            <!ELEMENT RES (#PCDATA)>
            <!ELEMENT PAGINA (#PCDATA)>
            <!-- Aquí termina el DTD -->
          ]>

          <BOE>
            <TITULO>BOLETIN OFICIAL DEL ESTADO</TITULO>
            <FECHA>
              <AAAA>2001</AAAA>
              <MM>03</MM>
              <DD>27</DD>
            </FECHA>
            <SUMARIO>
              <SECCION>
                <TIT-SECCION>I. Disposiciones Generales</TIT-SECCION>
                <APARTADO>
                  <ORGANISMO>MINISTERIO DE ASUNTOS EXTERIORES</ORGANISMO>
                  <PARRAFO>
                    <TEXTO>
                      <LEY>Ley 1/2001 de 5 de marzo</LEY>, de la cesión gratuita a las Comunidades Autónomas
                      el Impuesto sobre Animales y Cosas establecido por el <RD>REAL DECRETO 13/1991</RD>. </TEXTO>
                    <PAGINA>11290</PAGINA>
                  </PARRAFO>
                  <PARRAFO>
                    <TEXTO>
                      <ORDEN>ORDEN de 28 de febrero de 2001</ORDEN> por la ... </TEXTO>
                    <PAGINA>11290</PAGINA>
                    <PAGINA>11291</PAGINA>
                  </PARRAFO>
                </APARTADO>
                <APARTADO>
                  <ORGANISMO>MINISTERIO DE HACIENDA</ORGANISMO>
                  <PARRAFO>
                    <TEXTO>
                      <RD>REAL DECRETO 285/2001</RD>, de 19 de marzo, sobre ... </TEXTO>
                    <PAGINA>11291</PAGINA>
                    <PAGINA>11292</PAGINA>
                    <PAGINA>11293</PAGINA>
                    <PAGINA>11294</PAGINA>
                  </PARRAFO>
                </APARTADO>
                <APARTADO>
                  <ORGANISMO>MINISTERIO DE FOMENTO</ORGANISMO>
                  <PARRAFO>
                    <TEXTO> CORRECCIÓN de erratas de la Orden ... </TEXTO>
                    <PAGINA>11291</PAGINA>
                    <PAGINA>11292</PAGINA>
                    <PAGINA>11293</PAGINA>
                    <PAGINA>11294</PAGINA>
                  </PARRAFO>
                </APARTADO>
              </SECCION>
              <SECCION>
                <TIT-SECCION>II. Autoridades y Personal</TIT-SECCION>
                <APARTADO>
                  <ORGANISMO>CORTES GENERALES</ORGANISMO>
                  <PARRAFO>
                    <TEXTO>
                      <RES>RESOLUCIÓN de 19 de marzo de 2001</RES>, de la ... </TEXTO>
                    <PAGINA>11294</PAGINA>
                    <PAGINA>11295</PAGINA>
                  </PARRAFO>
                </APARTADO>
                <APARTADO>
                  <ORGANISMO>MINISTERIO DE ASUNTOS EXTERIORES</ORGANISMO>
                  <PARRAFO>
                    <TEXTO>
                      <RD>REAL DECRETO 314/2001</RD>, de 24 de marzo, por el ... </TEXTO>
                    <PAGINA>11291</PAGINA>
                    <PAGINA>11292</PAGINA>
                    <PAGINA>11293</PAGINA>
                    <PAGINA>11294</PAGINA>
                  </PARRAFO>
                </APARTADO>
              </SECCION>
            </SUMARIO>
          </BOE>
                    """;

      SAXParserFactory factory = SAXParserFactory.newInstance();
      factory.setValidating(false); // Desactiva validación DTD si hay errores

      SAXParser saxParser = factory.newSAXParser();
      InputSource is = new InputSource(new java.io.StringReader(xml));

      saxParser.parse(is, new DefaultHandler() {
        public void startElement(String uri, String localName, String qName, Attributes attributes) {
          System.out.println("Inicio de elemento: " + qName);
        }

        public void characters(char[] ch, int start, int length) {
          String texto = new String(ch, start, length).trim();
          if (!texto.isEmpty())
            System.out.println("Contenido: " + texto);
        }

        public void endElement(String uri, String localName, String qName) {
          System.out.println("Fin de elemento: " + qName);
        }
      });

    } catch (Exception e) {
      System.out.println("Error al parsear: " + e.getMessage());
    }
  }
}
