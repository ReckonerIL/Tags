package com.javarush.task.task20.task2027;

import java.util.*;

/* 
Кроссворд
*/
public class Solution {
    public static void main(String[] args) {
        int[][] crossword = new int[][]{
                {'f', 'd', 'e', 'r', 'l', 'k', 'p', 'c'},
                {'u', 's', 'a', 'm', 'e', 'o', 'd', 'h'},
                {'l', 'n', 'g', 'r', 'o', 'r', 'o', 'o'},
                {'m', 'l', 'p', 'r', 'r', 'h', 'r', 'r'},
                {'p', 'o', 'e', 'e', 'j', 'j', 'a', 'e'},
                {'p', 'o', 'e', 'e', 'j', 'm', 'w', 'k'}
        };
        List<Word> result = detectAllWords(crossword, "home", "same", "epr", "oprek", "plgml", "re", "horror");

        for (Word word : result) {
            System.out.println(word.getFullPath());
        }
        /*
        Ожидаемый результат
        home - (5, 3) - (2, 0)
        same - (1, 1) - (4, 1)
        */
    }

    public static List<Word> detectAllWords(int[][] crossword, String... words) {
        List<Word> result = new ArrayList<>();
        Node graph = new Node(crossword);
//        graph.setPossibleDirections(Node.Direction.NorthEast);
        Word word;
        for (String s : words) {
            word = new Word(s);
            char c = word.text.charAt(0);
            for (int i = 0; i < crossword.length; i++) {
                for (int j = 0; j < crossword[i].length; j++) {
                    if (graph.get(j, i).getValue() == c) {
//                        result.addAll(graph.get(j, i).findSameWordsOnLine(word));
                        result.addAll(graph.get(j, i).findSameWords(word));
                    }
                }
            }
        }

        return result;
    }

    public static class Word{
        private String text;
        private int startX;
        private int startY;
        private int endX;
        private int endY;
        private int[] path;

        public Word(String text) {
            this.text = text;
            path = new int[text.length() * 2];
        }

        public void setStartPoint(int i, int j) {
            startX = i;
            startY = j;
        }

        public void setEndPoint(int i, int j) {
            endX = i;
            endY = j;
        }

        @Override
        public String toString() {
            if (startX == 0) startX = path[0];
            if (startY == 0) startY = path[1];
            if (endX == 0) endX = path[path.length - 2];
            if (endY == 0) endY = path[path.length - 1];
            return String.format("%s - (%d, %d) - (%d, %d)", text, startX, startY, endX, endY);
        }

        public String getFullPath() {
            String result = text;
            for (int i = 0; i * 2 < path.length; i++) {
                result += " - (" + path[i * 2] + ", " + path[i * 2 + 1] + ")";
            }
            return result;
        }

        public Word clone() {
            Word word = new Word(text);
            word.path = path.clone();
            return word;
        }
    }

    public static class Node {
        private static ArrayList<Word> matches;
        private static char[] letters;
        private HashSet<Direction> possibleDirections;
        private char value;
        private int x;
        private int y;
        private Node north;
        private Node east;
        private Node south;
        private Node west;

        public Node(int[][] chars) {
            value = (char) chars[0][0];
            if (possibleDirections == null) {
                possibleDirections = new HashSet<>();
                resetDirections();
            }

            try {
                east = new Node(chars, x + 1, y, this, Direction.East);
            } catch (ArrayIndexOutOfBoundsException exc) { }
            try {
                south = new Node(chars, x, y + 1, this, Direction.South);
            } catch (ArrayIndexOutOfBoundsException exc) { }
        }

        private Node(int[][] chars, int x, int y, Node node, Direction direction) {
            value = (char) chars[y][x];
            this.x = x;
            this.y = y;
            if (direction == Direction.East) {
                west = node;
                possibleDirections = west.possibleDirections;
            }
            else if (direction == Direction.South) {
                north = node;
                possibleDirections = north.possibleDirections;
            }
            else throw new IllegalArgumentException(direction + " isn't allowed here.");

            try {
                east = north.east.south;
                east.west = this;
            } catch (NullPointerException exc) {
                try {
                    east = new Node(chars, x + 1, y, this, Direction.East);
                } catch (ArrayIndexOutOfBoundsException e1) {}
            }
            try {
                south = west.south.east;
                south.north = this;
            } catch (NullPointerException exc) {
                try {
                    south = new Node(chars, x, y + 1, this, Direction.South);
                } catch (ArrayIndexOutOfBoundsException e1) {}
            }
        }

        public Node get(int x, int y) {
            return find(0 - this.x, 0 - this.y).find(x, y);
        }

        private Node find(int x, int y) throws NullPointerException {
            if (x > 0 && y < 0) return getByDirection(Direction.NorthEast).find(x - 1, y + 1);
            if (x > 0 && y > 0) return getByDirection(Direction.SouthEast).find(x - 1, y - 1);
            if (x < 0 && y > 0) return getByDirection(Direction.SouthWest).find(x + 1, y - 1);
            if (x < 0 && y < 0) return getByDirection(Direction.NorthWest).find(x + 1, y + 1);
            if (y < 0) return north.find(x, y + 1);
            if (x > 0) return east.find(x - 1, y);
            if (y > 0) return south.find(x, y - 1);
            if (x < 0) return west.find(x + 1, y);
            return this;
        }

        public List<Word> findSameWordsOnLine(Word word) {
            ArrayList<Word> result = new ArrayList<>();
            Word temp = null;
            synchronized (possibleDirections) {
                for (Direction dir : possibleDirections) {
                    temp = findSameWordsOnLine(word.clone(), dir);
                    if (temp != null) result.add(temp);
                }
            }
            return result;
        }

        public Word findSameWordsOnLine(Word word, Direction direction) {
            Node node = getByDirection(direction);
            char[] letters = word.text.toCharArray();
            Stack<Node> stack = new Stack<>();
            stack.add(this);

            if (stack.size() < letters.length && letters[stack.size() - 1] == stack.peek().value && node != null) {
                word.path[(stack.size() - 1) * 2] = x;
                word.path[(stack.size() - 1) * 2 + 1] = y;
                return node.findSameWordsOnLine(word, direction, letters, stack);

            } else if (stack.size() == letters.length && letters[stack.size() - 1] == stack.peek().value) {
                word.path[(stack.size() - 1) * 2] = x;
                word.path[(stack.size() - 1) * 2 + 1] = y;
                return word;
            }
            return null;
        }

        private Word findSameWordsOnLine(Word word, Direction direction, char[] letters, Stack<Node> stack) {
            Node node = getByDirection(direction);
            stack.add(this);

            if (stack.size() < letters.length && letters[stack.size() - 1] == stack.peek().value && node != null){
                word.path[(stack.size() - 1) * 2] = x;
                word.path[(stack.size() - 1) * 2 + 1] = y;
                return node.findSameWordsOnLine(word, direction, letters, stack);
            } else if (stack.size() == letters.length && letters[stack.size() - 1] == stack.peek().value) {
                word.path[(stack.size() - 1) * 2] = x;
                word.path[(stack.size() - 1) * 2 + 1] = y;
                return word;
            }
            return null;
        }

        public synchronized List<Word> findSameWords(Word word) {
            matches = new ArrayList<>();
            letters = word.text.toCharArray();
            Word temp = word.clone();
            Stack<Node> stack = new Stack<>();
            stack.add(this);
            if (stack.size() < letters.length && letters[stack.size() - 1] == stack.peek().value) {
                temp.path[(stack.size() - 1) * 2] = x;
                temp.path[(stack.size() - 1) * 2 + 1] = y;
                synchronized (possibleDirections) {
                    for (Direction dir : possibleDirections) {
                        if (getByDirection(dir) != null) matches.addAll(getByDirection(dir).findSameWords(temp.clone(), stack));
                    }
                }
            } else if (stack.size() == letters.length && letters[stack.size() - 1] == stack.peek().value) {
                temp.path[(stack.size() - 1) * 2] = x;
                temp.path[(stack.size() - 1) * 2 + 1] = y;
                matches.add(temp);
            }
            List<Word> result = matches;
            matches = null;
            return result;
        }

        private List<Word> findSameWords(Word word, Stack<Node> stack) {
            List<Word> result = new ArrayList<>();
            Word temp = word.clone();
            stack.add(this);
            if (stack.size() < letters.length && letters[stack.size() - 1] == stack.peek().value) {
                temp.path[(stack.size() - 1) * 2] = x;
                temp.path[(stack.size() - 1) * 2 + 1] = y;
                synchronized (possibleDirections) {
                    for (Direction dir : possibleDirections) {
                        if (getByDirection(dir) != null && !stack.contains(getByDirection(dir)))
                            result.addAll(getByDirection(dir).findSameWords(temp.clone(), stack));
                    }
                }
            } else if (stack.size() == letters.length && letters[stack.size() - 1] == stack.peek().value) {
                temp.path[(stack.size() - 1) * 2] = x;
                temp.path[(stack.size() - 1) * 2 + 1] = y;
                result.add(temp);
            }
            stack.pop();
            return result;
        }

        public char getValue() {
            return value;
        }

        public void setPossibleDirections(Direction... directions) {
            if (directions == null) throw new IllegalArgumentException();
            synchronized (possibleDirections) {
                for (Direction dir : directions) {
                    if (!possibleDirections.contains(dir)) possibleDirections.add(dir);
                }

                Iterator<Direction> iterator = possibleDirections.iterator();
                Direction direction = null;
                boolean delete = false;
                while (iterator.hasNext()) {
                    direction = iterator.next();
                    for (Direction dir : directions) {
                        if (dir == direction) {
                            delete = false;
                            break;
                        }
                    }
                    if (delete) iterator.remove();
                    delete = true;
                }
            }
        }

        public void resetDirections() {
            setPossibleDirections(Direction.North, Direction.NorthEast, Direction.East, Direction.SouthEast,
                    Direction.South, Direction.SouthWest, Direction.West, Direction.NorthWest);
        }

        public Node getByDirection(Direction direction) {
            if (direction == Direction.North && north != null) return north;
            if (direction == Direction.NorthEast && north != null && north.east != null) return north.east;
            if (direction == Direction.East && east != null) return east;
            if (direction == Direction.SouthEast && south != null && south.east != null) return south.east;
            if (direction == Direction.South && south != null) return south;
            if (direction == Direction.SouthWest && south != null && south.west != null) return south.west;
            if (direction == Direction.West && west != null) return west;
            if (direction == Direction.NorthWest && north != null && north.west != null) return north.west;
            return null;
        }

        @Override
        public String toString() {
            return String.valueOf(value);
        }

        public static enum Direction {
            North,
            NorthEast,
            East,
            SouthEast,
            South,
            SouthWest,
            West,
            NorthWest;
        }
    }
}
