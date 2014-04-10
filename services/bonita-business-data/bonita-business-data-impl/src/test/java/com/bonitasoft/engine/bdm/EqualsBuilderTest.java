package com.bonitasoft.engine.bdm;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.util.Date;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JType;

/**
 * @author Romain Bioteau
 */
public class EqualsBuilderTest extends CompilableCode {

    private EqualsBuilder equalsBuilder;

    private CodeGenerator codeGenerator;

    private File destDir;

    @Before
    public void setUp() throws Exception {
        codeGenerator = new CodeGenerator();
        equalsBuilder = new EqualsBuilder();
        destDir = new File(System.getProperty("java.io.tmpdir"), "generationDir");
        destDir.mkdirs();
    }

    @After
    public void tearDown() throws Exception {
        destDir.delete();
    }

    @Test
    public void shouldGenerate_AddEqualsJMethodInDefinedClass() throws Exception {
        final JDefinedClass definedClass = codeGenerator.addClass("org.bonitasoft.Entity");
        codeGenerator.addField(definedClass, "name", String.class);
        codeGenerator.addField(definedClass, "age", Integer.class);
        codeGenerator.addField(definedClass, "returnDate", codeGenerator.getModel().ref(Date.class));
        final JMethod equalsMethod = equalsBuilder.generate(definedClass);
        assertThat(equalsMethod).isNotNull();
        assertThat(equalsMethod.name()).isEqualTo("equals");
        assertThat(equalsMethod.hasSignature(new JType[] { codeGenerator.getModel().ref(Object.class.getName()) })).isTrue();
        assertThat(equalsMethod.type().fullName()).isEqualTo(boolean.class.getName());

        final JBlock body = equalsMethod.body();
        assertThat(body).isNotNull();
        assertThat(body.getContents()).isNotEmpty();

        codeGenerator.getModel().build(destDir);
        assertCompilationSuccessful(new File(destDir, "org" + File.separatorChar + "bonitasoft" + File.separatorChar + "Entity.java"));
    }

}
