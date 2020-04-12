package com.freetmp.mbg.merge.statement;

import com.freetmp.mbg.merge.AbstractMerger;
import com.github.javaparser.ast.stmt.CatchClause;

/**
 * Created by LiuPin on 2015/5/13.
 */
public class CatchClauseMerger extends AbstractMerger<CatchClause> {

  @Override public CatchClause doMerge(CatchClause first, CatchClause second) {
    CatchClause cc = new CatchClause();
    cc.setCatchBlock(mergeSingle(first.getCatchBlock(),second.getCatchBlock()));
    cc.setParam(mergeSingle(first.getParam(),second.getParam()));
    return cc;
  }

  @Override public boolean doIsEquals(CatchClause first, CatchClause second) {

    if(!isEqualsUseMerger(first.getParam(),second.getParam())) {
      return false;
    }
    return isEqualsUseMerger(first.getCatchBlock(), second.getCatchBlock());
  }
}
