package com.freetmp.mbg.merge.expression;

import com.freetmp.mbg.merge.AbstractMerger;
import com.github.javaparser.ast.TypeArguments;
import com.github.javaparser.ast.expr.MethodReferenceExpr;
import com.github.javaparser.ast.type.Type;

import java.util.List;

/**
 * Created by LiuPin on 2015/5/13.
 */
public class MethodReferenceExprMerger extends AbstractMerger<MethodReferenceExpr> {

    @Override
    public MethodReferenceExpr doMerge(MethodReferenceExpr first, MethodReferenceExpr second) {
        MethodReferenceExpr mre = new MethodReferenceExpr();

        mre.setScope(mergeSingle(first.getScope(), second.getScope()));
        mre.setIdentifier(first.getIdentifier());
        List<Type> types = mergeCollectionsInOrder(first.getTypeArguments().getTypeArguments(), second.getTypeArguments().getTypeArguments());
        mre.setTypeArguments(TypeArguments.withArguments(types));

        return mre;
    }

    @Override
    public boolean doIsEquals(MethodReferenceExpr first, MethodReferenceExpr second) {

        if (!first.getIdentifier().equals(second.getIdentifier())) {
            return false;
        }
        if (!isEqualsUseMerger(first.getScope(), second.getScope())) {
            return false;
        }
        return isEqualsUseMerger(first.getTypeArguments().getTypeArguments(), second.getTypeArguments().getTypeArguments());
    }
}
