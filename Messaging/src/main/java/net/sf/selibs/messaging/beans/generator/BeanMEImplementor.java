package net.sf.selibs.messaging.beans.generator;

import com.github.javaparser.ASTHelper;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.ModifierSet;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.expr.ArrayCreationExpr;
import com.github.javaparser.ast.expr.ArrayInitializerExpr;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MarkerAnnotationExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.type.Type;
import java.util.LinkedList;
import java.util.List;
import javax.inject.Inject;
import net.sf.selibs.messaging.beans.BeanME;
import org.simpleframework.xml.Element;

public class BeanMEImplementor implements MethodImplementor, FieldAdder {

    public static final String PARAMS_TEMPLATE = "Object[] params = {%s}";
    public static final String CLASSES_TEMPLATE = "Class[] paramClasses = {%s}";
    public static final String EXCHANGE_TEMPLATE = "Object result = #fieldName#.exchange(#beanName#,\"#methodName#\",paramClasses,params)";
    public static final String RETURN_TEMPLATE = "return (#returnType#)result";

    public String bmeName = "bme";
    public String beanName = "beanName";

    @Override
    public void implementMethod(MethodDeclaration interfaceMethod, MethodDeclaration implementation) throws GeneratorException {
        if (!this.isThrowsExceptionPresent(interfaceMethod)) {
            throw new GeneratorException("Implemented methods must throw Exception");
        }
        //add a body to the method
        BlockStmt block = new BlockStmt();
        //add a statement do the method body
        //Object[] params = {%s}
        ASTHelper.addStmt(block, this.createParamsExpression(interfaceMethod));
        //Class[] paramClasses = {%s}
        ASTHelper.addStmt(block, this.createPClassesExpression(interfaceMethod));
        //Object result = bme.exchange(beanName,#methodName#,paramClasses,params)s)
        String exchangeStr = EXCHANGE_TEMPLATE.replaceFirst("#methodName#", interfaceMethod.getName());
        exchangeStr = exchangeStr.replaceFirst("#fieldName#", bmeName);
        exchangeStr = exchangeStr.replaceFirst("#beanName#", this.beanName);
        NameExpr exchangeExpr = new NameExpr(exchangeStr);
        ASTHelper.addStmt(block, exchangeExpr);
        //return (#returnType#) result
        String returnType = interfaceMethod.getType().toString().trim();
        if (!returnType.equalsIgnoreCase("void")) {
            String returnStr = RETURN_TEMPLATE.replaceFirst("#returnType#", getWrapperType(returnType));
            NameExpr returnExpr = new NameExpr(returnStr);
            ASTHelper.addStmt(block, returnExpr);
        }

        implementation.setBody(block);
    }

    protected AssignExpr createParamsExpression(MethodDeclaration interfaceMethod) {
        //new Object[]{p1,p2,...}
        ArrayCreationExpr paramsExpr = new ArrayCreationExpr();
        paramsExpr.setArrayCount(1);
        paramsExpr.setType(ASTHelper.createReferenceType("Object", 0));
        List<Expression> pNames = new LinkedList();
        for (Parameter p : interfaceMethod.getParameters()) {
            pNames.add(new NameExpr(p.getChildrenNodes().get(0).toString()));
        }
        paramsExpr.setInitializer(new ArrayInitializerExpr(pNames));
        //Object[] params = new Object[]{...};
        AssignExpr assign = new AssignExpr(new NameExpr("Object[] params"), paramsExpr, AssignExpr.Operator.assign);
        return assign;
    }

    protected AssignExpr createPClassesExpression(MethodDeclaration interfaceMethod) {
        //new Class[]{p1.class,p2.class,...}
        ArrayCreationExpr paramsExpr = new ArrayCreationExpr();
        paramsExpr.setArrayCount(1);
        paramsExpr.setType(ASTHelper.createReferenceType("Class", 0));
        List<Expression> pNames = new LinkedList();
        for (Parameter p : interfaceMethod.getParameters()) {
            pNames.add(new NameExpr(p.getChildrenNodes().get(1).toString() + ".class"));
        }
        paramsExpr.setInitializer(new ArrayInitializerExpr(pNames));
        //Object[] params = new Object[]{...};
        AssignExpr assign = new AssignExpr(new NameExpr("Class[] paramClasses"), paramsExpr, AssignExpr.Operator.assign);
        return assign;
    }

    public static String getWrapperType(String type) {
        if (type.equals("boolean")) {
            return "Boolean";
        }
        if (type.equals("byte")) {
            return "Byte";
        }
        if (type.equals("short")) {
            return "Short";
        }
        if (type.equals("int")) {
            return "Integer";
        }
        if (type.equals("long")) {
            return "Long";
        }
        if (type.equals("char")) {
            return "Character";
        }
        return type;
    }

    @Override
    public void addFields(ClassOrInterfaceDeclaration interfaceType, ClassOrInterfaceDeclaration implType) {
        this.addBeanMEField(implType);
        this.addBeanNameField(implType);
    }

    protected void addBeanMEField(ClassOrInterfaceDeclaration implType) {
        Type bmeType = ASTHelper.createReferenceType(BeanME.class.getName(), 0);
        FieldDeclaration bmeField = ASTHelper.createFieldDeclaration(ModifierSet.PUBLIC, bmeType, bmeName);
        List<AnnotationExpr> annotations = new LinkedList();
        annotations.add(new MarkerAnnotationExpr(new NameExpr(Inject.class.getName())));
        bmeField.setAnnotations(annotations);
        ASTHelper.addMember(implType, bmeField);
    }

    protected void addBeanNameField(ClassOrInterfaceDeclaration implType) {
        Type bnType = ASTHelper.createReferenceType("String", 0);
        FieldDeclaration bnField = ASTHelper.createFieldDeclaration(ModifierSet.PUBLIC, bnType, this.beanName);
        List<AnnotationExpr> annotations = new LinkedList();
        annotations.add(new MarkerAnnotationExpr(new NameExpr(Element.class.getName())));
        bnField.setAnnotations(annotations);
        ASTHelper.addMember(implType, bnField);
    }

    protected boolean isThrowsExceptionPresent(MethodDeclaration interfaceMethod) {
        for (NameExpr throwExpr : interfaceMethod.getThrows()) {
            if (throwExpr.getName().contains("Exception")) {
                return true;
            }
        }
        return false;
    }

}
