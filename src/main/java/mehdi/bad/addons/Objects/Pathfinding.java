package mehdi.bad.addons.Objects;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3i;
import net.minecraft.world.World;

import java.util.*;

public class Pathfinding {

    public static class Node {
        BlockPos position;
        float cost;

        public Node(BlockPos position, float cost) {
            this.position = position;
            this.cost = cost;
        }
    }

    private static final int[][] DIRECTIONS = {
            {1, 0, 0}, {-1, 0, 0}, {0, 1, 0}, {0, -1, 0}, {0, 0, 1}, {0, 0, -1},
    };

    public static Path getShortestPathBetween(World world, BlockPos start, BlockPos end, float weight) {
        PriorityQueue<Node> openSet = new PriorityQueue<>(Comparator.comparingDouble(n -> n.cost));
        Map<BlockPos, BlockPos> cameFrom = new HashMap<>();
        Map<BlockPos, Float> costSoFar = new HashMap<>();
        Set<BlockPos> closedSet = new HashSet<>();

        openSet.add(new Node(start, 0));
        costSoFar.put(start, 0f);

        while (!openSet.isEmpty()) {
            Node current = openSet.poll();

            if (current.position.equals(end)) {
                return reconstructPath(cameFrom, start, end);
            }

            closedSet.add(current.position);

            for (int[] dir : DIRECTIONS) {
                BlockPos neighbor = current.position.add(dir[0], dir[1], dir[2]);

                if (!isValidBlock(world, neighbor) || closedSet.contains(neighbor)) continue;

                float newCost = costSoFar.get(current.position) + getMovementCost(world, neighbor, weight);
                if (!costSoFar.containsKey(neighbor) || newCost < costSoFar.get(neighbor)) {
                    costSoFar.put(neighbor, newCost);
                    float priority = newCost + heuristic(neighbor, end);
                    openSet.add(new Node(neighbor, priority));
                    cameFrom.put(neighbor, current.position);
                }
            }
        }

        return null;  // No path found
    }

    private static Path reconstructPath(Map<BlockPos, BlockPos> cameFrom, BlockPos start, BlockPos end) {
        List<BlockPos> path = new ArrayList<>();
        BlockPos current = end;
        while (!current.equals(start)) {
            path.add(current);
            current = cameFrom.get(current);
        }
        path.add(start);
        return new Path(path);
    }

    private static boolean isValidBlock(World world, BlockPos pos) {
        Block block = world.getBlockState(pos).getBlock();
        return block.isPassable(world, pos) && block.getMaterial() != Material.lava;
    }

    private static float getMovementCost(World world, BlockPos pos, float weight) {
        Block block = world.getBlockState(pos).getBlock();
        float baseCost = block.getBlockHardness(world, pos);
        return baseCost * weight;
    }

    private static float heuristic(BlockPos a, BlockPos b) {
        return (float) a.distanceSq(new Vec3i(b.getX(), b.getY(), b.getZ()));
    }

    public static class Path {
        private final List<BlockPos> path;

        public Path(List<BlockPos> path) {
            this.path = path;
        }

        public List<BlockPos> getPath() {
            return path;
        }
    }
}