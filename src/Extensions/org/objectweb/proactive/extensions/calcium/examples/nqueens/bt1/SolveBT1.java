/*
 * ################################################################
 *
 * ProActive Parallel Suite(TM): The Java(TM) library for
 *    Parallel, Distributed, Multi-Core Computing for
 *    Enterprise Grids & Clouds
 *
 * Copyright (C) 1997-2012 INRIA/University of
 *                 Nice-Sophia Antipolis/ActiveEon
 * Contact: proactive@ow2.org or contact@activeeon.com
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; version 3 of
 * the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 * USA
 *
 * If needed, contact us to obtain a release under GPL Version 2 or 3
 * or a different license than the AGPL.
 *
 *  Initial developer(s):               The ProActive Team
 *                        http://proactive.inria.fr/team_members.htm
 *  Contributor(s):
 *
 * ################################################################
 * $$PROACTIVE_INITIAL_DEV$$
 */
package org.objectweb.proactive.extensions.calcium.examples.nqueens.bt1;

import org.objectweb.proactive.extensions.calcium.examples.nqueens.Board;
import org.objectweb.proactive.extensions.calcium.examples.nqueens.Result;
import org.objectweb.proactive.extensions.calcium.examples.nqueens.SolveBoard;
import org.objectweb.proactive.extensions.calcium.system.SkeletonSystem;


public class SolveBT1 extends SolveBoard {
    public SolveBT1() {
        super();
    }

    public Result execute(Board board, SkeletonSystem system) {
        n1 = board.n - 1;
        n2 = n1 - 1;
        BoardBT1 boardBT1 = (BoardBT1) board;
        Result res = new Result(board.n);
        backtrack1(res, boardBT1, boardBT1.row, boardBT1.left, boardBT1.down, boardBT1.right);
        return mixBoard(res, n1, n2);
    }

    private void backtrack1(Result res, BoardBT1 board, int y, int left, int down, int right) {
        int bitmap = board.mask & ~(left | down | right);
        int bit;
        int firstColumn;
        int lastColumn;

        if (y == n1) {
            if (bitmap != 0) {
                board.board[y] = bitmap;
                //count8();
                res.solutions[position(board.board[0])]++;
                res.solutions[position(board.board[n1])]++;
                for (firstColumn = 0; (board.board[firstColumn] & 1) == 0; firstColumn++)
                    ;
                for (lastColumn = 1; (board.board[lastColumn] & board.topbit) == 0; lastColumn++)
                    ;
                res.solutions[firstColumn]++;
                res.solutions[lastColumn]++;
            }
        } else {
            if (y < board.bound1) {
                bitmap &= 0xFFFFFFFD; // 1111...01
            }
            while (bitmap != 0) {
                bitmap ^= (board.board[y] = bit = -bitmap & bitmap);
                backtrack1(res, board, y + 1, (left | bit) << 1, down | bit, (right | bit) >> 1);
            }
        }
    }
}
