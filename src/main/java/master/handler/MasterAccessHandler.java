/*
author:chxy
data:2020/5/20
description:
*/
package master.handler;


import master.agent.MasterAgent;
import miscellaneous.AbstractHandler;
import master.staticresource.Slave;

import java.util.*;


public abstract class MasterAccessHandler extends AbstractHandler {

    protected static final Random RANDOM = new Random();

    //采用随机算法，选出一个结点作为访问结点
    protected static Slave selectSlave(List<Integer> ids) {
        int r = RANDOM.nextInt(ids.size());
        int id = ids.get(r);
        return MasterAgent.slaves.get(r);
    }

    //采用随机算法，选出一批结点作为存储结点
    //选出来的结点个数有可能少于DUPLICATIONNUMS
    protected static List<Slave> selectSlaves(Collection<Slave> slaves) {

        List<Slave> candidates = new ArrayList<>();
        List<Integer> nums = new ArrayList<>();
        if (slaves.size() <= MasterAgent.DUPLICATIONNUMS) {
            candidates.addAll(slaves);
            return candidates;
        }

        while (nums.size() < MasterAgent.DUPLICATIONNUMS) {
            int i = RANDOM.nextInt(slaves.size());
            if (!nums.contains(i))
                nums.add(i);
        }

        Collections.sort(nums);

        int counter = -1, k = 0;
        for (Slave slave:slaves) {
            counter++;
            if (counter == nums.get(k)) {
                candidates.add(slave);
                k++;
                if (k > nums.size() - 1) break;
            }
        }
        return candidates;
    }
}


