package net.sf.selibs.messaging.beans.generator;

import com.github.javaparser.ASTHelper;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.ModifierSet;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.expr.MarkerAnnotationExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import java.util.LinkedList;
import java.util.List;
import lombok.Data;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Data
public class InterfaceImplementor {

    protected MethodImplementor methodImplementor;
    protected FieldAdder fieldAdder;
    protected String packageSuffix = "impl";
    protected String nameSuffix = "Impl";

    public CompilationUnit makeImpl(CompilationUnit interfaceCU) throws GeneratorException {
        CompilationUnit impl = new CompilationUnit();
        //add package name
        String packageName = interfaceCU.getPackage().getName().toString() + "." + packageSuffix;
        impl.setPackage(new PackageDeclaration(ASTHelper.createNameExpr(packageName)));
        //add imports
        List<ImportDeclaration> imports = new LinkedList();
        imports.addAll(interfaceCU.getImports());
        imports.add(new ImportDeclaration(ASTHelper.createNameExpr(
                interfaceCU.getPackage().getName().toString()),
                false,
                true));
        impl.setImports(imports);
        List<ClassOrInterfaceType> implement = new LinkedList();
        //add implements 
        implement.add(new ClassOrInterfaceType(this.getInterfaceName(interfaceCU)));
        //create new implementation class
        //ClassOrInterfaceDeclaration(final int modifiers, final boolean isInterface, final String name)
        ClassOrInterfaceDeclaration type = new ClassOrInterfaceDeclaration(
                ModifierSet.PUBLIC,
                false,
                this.getInterfaceName(interfaceCU) + nameSuffix);
        type.setImplements(implement);
        //add  Root annotation
        List<AnnotationExpr> typeAnns = new LinkedList();
        typeAnns.add(new MarkerAnnotationExpr(new NameExpr(Root.class.getName())));
        type.setAnnotations(typeAnns);
        ASTHelper.addTypeDeclaration(impl, type);
        //add some fields
        this.fieldAdder.addFields((ClassOrInterfaceDeclaration) this.getInterfaceType(interfaceCU), type);
        //implement methods
        for (MethodDeclaration method : this.getInterfaceMethods(interfaceCU)) {

            //add @Override
            List<AnnotationExpr> annotations = new LinkedList();
            AnnotationExpr override = new MarkerAnnotationExpr(ASTHelper.createNameExpr("Override"));
            annotations.add(override);
            //public MethodDeclaration(int modifiers,Type type,String name)
            MethodDeclaration methodImpl = new MethodDeclaration(
                    ModifierSet.PUBLIC, method.getType(), method.getName()
            );
            methodImpl.setAnnotations(annotations);
            //add throws
            methodImpl.setThrows(method.getThrows());
            //add parameters
            methodImpl.setParameters(method.getParameters());
            //add body
            methodImplementor.implementMethod(method, methodImpl);
            ASTHelper.addMember(type, methodImpl);
        }

        return impl;

    }

    protected String getInterfaceName(CompilationUnit interfaceCU) {
        return interfaceCU.getTypes().get(0).getName();
    }

    protected TypeDeclaration getInterfaceType(CompilationUnit interfaceCU) {
        return interfaceCU.getTypes().get(0);
    }

    protected List<MethodDeclaration> getInterfaceMethods(CompilationUnit interfaceCU) {
        List<MethodDeclaration> methods = new LinkedList();
        List<BodyDeclaration> members = interfaceCU.getTypes().get(0).getMembers();
        for (BodyDeclaration member : members) {
            if (member instanceof MethodDeclaration) {
                MethodDeclaration method = (MethodDeclaration) member;
                methods.add(method);
            }
        }
        return methods;
    }
}
