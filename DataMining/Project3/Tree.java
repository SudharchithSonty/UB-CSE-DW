/**
 * Created by Amair on 09-12-2015.
 */
public class Tree {
    public Node root=null;
    public void addNode(Node newNode) {
        if(root == null){
            root = newNode;
        }
        else{
            Node current_node = root;
            while(current_node != null){
                if(newNode.begin ==  current_node.split_point){
                    current_node.righNode = newNode;
                    break;
                }
                if(newNode.end == current_node.split_point){
                    current_node.leftNode = newNode; //if the end are same
                    break;
                }
                else{
                    if(newNode.begin >= current_node.split_point){
                        if(current_node.righNode == null){
                            current_node.righNode = newNode;
                            break;
                        }
                        current_node = current_node.righNode; //place is towards right child
                    }
                    else if(newNode.end < current_node.split_point){
                        if(current_node.leftNode == null){
                            current_node.leftNode = newNode; //found the position
                            break;
                        }
                        current_node = current_node.leftNode; //keep traversing on left
                    }
                }

            }
        }
    }

/*    public void print_tree(Node node){
        Node current_node = node;
        if (current_node != null) {
            print_tree(current_node.leftChild);
            System.out.println("Key: "+(float)current_node.key);
            System.out.println("Col: "+current_node.column);
            System.out.println(current_node.node);
            System.out.println("Left: "+current_node.leftLabel);
            System.out.println("Right: "+current_node.rightLabel);
            System.out.print("\n");
            print_tree(focusNode.rightChild);
        }
    }*/
}
