public class DesignQA {
    /*
    You have a browser of one tab where you start on the homepage and you can visit another URL, get back in the history number of steps or move forward in the history number of steps. The task is to design a data structure and implement the functionality of visiting a URL starting from the homepage and moving back and forward in the history. The following functionalities should be covered:

    visit(url) : Visits a URL given as string
    forward(steps) : Takes 'steps' forward.
    back(steps) : Takes 'steps' backward.
    Note: The starting page of the tab will always be the homepage.
     */

    public static void main(String[] args) {

        // Input case :
        String homepage = "gfg.org";

        // Initialise the object of Browser History
        BrowserHistory obj = new BrowserHistory(homepage);

        String url = "google.com";
        obj.visit(url);

        url = "facebook.com";
        obj.visit(url);

        url = "youtube.com";
        obj.visit(url);

        System.out.println(obj.back(1));
        System.out.println(obj.back(1));
        System.out.println(obj.forward(1));
        obj.visit("linkedin.com");
        System.out.println(obj.forward(2));
        System.out.println(obj.back(2));
        System.out.println(obj.back(7));
    }

    private static class BrowserHistory {
        private DNode curr;

        public BrowserHistory(String homepage) {
            this.curr = new DNode(homepage, null);
        }

        public void visit(String url) {
            var node = new DNode(url, curr);
            curr.addNext(node);
            curr = node;
        }

        public String back(int x) {
            while (curr.prev != null && x != 0) {
                curr = curr.getPrev();
                x--;
            }
            return curr.getSite();
        }

        public String forward(int x) {
            while (curr.next != null && x != 0) {
                curr = curr.getNext();
                x--;
            }
            return curr.getSite();
        }
    }
        public static class DNode {
            private String site;
            DNode prev, next;

            public String getSite() {
                return site;
            }

            public DNode(String site, DNode prev) {
                this.site = site;
                this.prev = prev;
            }

            public void addNext(DNode node) {
                this.next = node;
            }

            public DNode getNext() {
                return next;
            }

            public DNode getPrev() {
                return prev;
            }
        }
    }

