package net.sf.selibs.messaging.beans.generator;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.expr.AnnotationExpr;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import net.sf.selibs.utils.io.FileUtils;
import net.sf.selibs.utils.misc.UHelper;

public class PathImplementor {

    public InterfaceImplementor ii;
    public String encoding = "UTF-8";

    public PathImplementor() {
        ii = new InterfaceImplementor();
        ii.setFieldAdder(new BeanMEImplementor());
        ii.setMethodImplementor(new BeanMEImplementor());
    }

    public void processPath(File path) throws Exception {
        for (File f : path.listFiles()) {
            if (f.isDirectory()) {
                this.processPath(f);
            } else {
                this.processFile(f);
            }
        }
    }

    protected void processFile(File file) throws FileNotFoundException, ParseException, GeneratorException, UnsupportedEncodingException, IOException {
        if (!file.getName().endsWith(".java")) {
            return;
        }
        FileInputStream in = new FileInputStream(file);
        try {
            CompilationUnit interfaceCU = JavaParser.parse(in);
            this.processCU(interfaceCU);
            CompilationUnit impl = this.processCU(interfaceCU);
            if (impl == null) {
                return;
            }
            File implDir = new File(file.getParentFile(), ii.getPackageSuffix());
            implDir.mkdirs();
            String implFileName = file.getName().split("\\.")[0] + ii.getNameSuffix() + ".java";
            File implFile = new File(implDir, implFileName);
            FileUtils.writeByteArray(implFile.getAbsolutePath(), impl.toString().getBytes(encoding));
        } finally {
            UHelper.close(in);
        }

    }

    protected CompilationUnit processCU(CompilationUnit interfaceCU) throws GeneratorException {
        ClassOrInterfaceDeclaration iDeclaration = (ClassOrInterfaceDeclaration) interfaceCU.getTypes().get(0);
        if (!this.checkIfImplementPresent(iDeclaration)) {
            return null;
        }
        if (!iDeclaration.isInterface()) {
            throw new GeneratorException("Only interface implementation is supported");
        }
        return this.ii.makeImpl(interfaceCU);
    }

    protected boolean checkIfImplementPresent(ClassOrInterfaceDeclaration iDeclaration) {
        for (AnnotationExpr ann : iDeclaration.getAnnotations()) {
            if (ann.getName().getName().contains(Implement.class.getSimpleName())) {
                return true;
            }
        }
        return false;
    }

}
