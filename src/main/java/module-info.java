module proforma.varproforma {
    requires java.logging;
    
    requires transitive proforma.xml21;
    
    requires static javafx.controls;
    requires static javafx.graphics;
    requires static javafx.web;

    requires org.graalvm.sdk;
    requires com.github.mustachejava;
    
    exports proforma.varproforma;
    exports proforma.varproforma.util;
    exports proforma.varproforma.fx;
    opens proforma.varproforma;

    // The following are needed for tests only.
    //requires static proforma.util;
    // This is a bad workaround to get rid off compile errors in eclipse for the tests to be compiled on the classpath
    //requires static junit;
    

}
