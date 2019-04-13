package com.freetmp.mbg.merge;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ModifierSet;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.intellij.openapi.project.Project;
import org.xstudio.plugins.idea.generator.SourcesHelper;

import java.io.File;
import java.io.IOException;

/**
 * Created by LiuPin on 2015/3/27.
 */
public class CompilationUnitMerger extends AbstractMerger<CompilationUnit> {

    /**
     * Util method to make source merge more convenient
     *
     * @param first  merge params, specifically for the existing source
     * @param second merge params, specifically for the new source
     * @param project
     * @return merged result
     * @throws ParseException cannot parse the input params
     */
    public static String merge(String first, File second, Project project) throws ParseException, IOException {
        JavaParser.setDoNotAssignCommentsPreceedingEmptyLines(false);
        CompilationUnit cu1 = JavaParser.parse(SourcesHelper.stringToStream(first, "gbk"));
//        CompilationUnit cu1 = JavaParser.parse(SourcesHelper.stringToStream(first, "utf-8"));
        CompilationUnit cu2 = JavaParser.parse(second, "utf-8");
        AbstractMerger<CompilationUnit> merger = AbstractMerger.getMerger(CompilationUnit.class);
        CompilationUnit result = merger.merge(cu1, cu2);
        return result.toString();
    }

    @Override
    public CompilationUnit doMerge(CompilationUnit first, CompilationUnit second) {

        CompilationUnit unit = new CompilationUnit();

        unit.setPackage(mergeSingle(first.getPackage(), second.getPackage()));

        unit.setImports(mergeCollections(first.getImports(), second.getImports()));

        unit.setTypes(mergeCollections(first.getTypes(), second.getTypes()));

        return unit;
    }

    @Override
    public boolean doIsEquals(CompilationUnit first, CompilationUnit second) {
        // 检测包声明
        if (!isEqualsUseMerger(first.getPackage(), second.getPackage())) {
            return false;
        }

        // 检查公共类声明
        for (TypeDeclaration outer : first.getTypes()) {
            for (TypeDeclaration inner : second.getTypes()) {
                if (ModifierSet.isPublic(outer.getModifiers()) && ModifierSet.isPublic(inner.getModifiers())) {
                    if (outer.getName().equals(inner.getName())) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

}
