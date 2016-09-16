package net.sf.selibs.messaging.beans.generator;

import com.github.javaparser.ast.body.MethodDeclaration;

public interface MethodImplementor {

    public void implementMethod(
            MethodDeclaration interfaceMethod,
            MethodDeclaration implementation) throws GeneratorException;
}
