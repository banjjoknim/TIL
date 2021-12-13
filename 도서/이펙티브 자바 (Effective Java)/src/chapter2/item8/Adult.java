package chapter2.item8;

class Adult {
    public static void main(String[] args) {
        // 잘 짜인 클라이언트 코드
        try (Room room = new Room(7)) {
            System.out.println("안녕~");
        }

//        ===================================================

        // 잘못된(결코 방 청소를 하지 않는) 클라이언트 코드
        Room room = new Room(99);
        System.out.println("아무렴");
    }
}
