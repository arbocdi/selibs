package net.sf.selibs.messaging.beans.generator;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;

public interface FieldAdder {

    void addFields(ClassOrInterfaceDeclaration interfaceType,
            ClassOrInterfaceDeclaration implType);
}
