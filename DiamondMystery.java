import java.util.*;

abstract class Cards {
    private String name;
    public Cards (String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }
    public String getDetails() {
        return "Card: " + name;
    }
}

class CharacterCards extends Cards {
    public CharacterCards (String name) {
        super(name);
    }
    @Override
    public String getDetails() {
        return "Character Card: " + getName();
    }
}
class PlaceCards extends Cards {
    public PlaceCards (String name) {
        super(name);
    }
    @Override
    public String getDetails() {
        return "Place Card: " + getName();
    }
}
class RoomCards extends Cards {
    private int roomNo;
    public RoomCards (String name, int roomNo) {
        super(name);
        this.roomNo = roomNo;
    }
    public int getRoomNo() {
        return roomNo;
    }
    @Override
    public String getDetails() {
        return "Room Card: " + getName() + " (Room No. " + roomNo + ")";
    }
}

class Players {
    private String name;
    private RoomCards currentRoom;
    private RoomCards previousRoom;
    private List<Cards> hand;

    public Players(String name) {
        this.name = name;
        this.hand = new ArrayList<>();
    }
    public String getName() {
        return name;
    }
    public RoomCards getCurrentRoom() {
        return currentRoom;
    }
    public void setCurrentRoom(RoomCards currentRoom) {
        this.previousRoom = this.currentRoom;
        this.currentRoom = currentRoom;
    }
    public RoomCards getPreviousRoom() {
        return previousRoom;
    }
    public List<Cards> getHand() {
        return hand;
    }
    public void makeGuess(String characterGuess, String placeGuess, RoomCards roomGuess) {
        System.out.println(name + " guesses: " + characterGuess + " in " + placeGuess + " in " + roomGuess.getName());
    }

    public Cards revealCard(String characterGuess, String placeGuess, RoomCards roomGuess) {
        for (Cards card : hand) {
            if (card instanceof CharacterCards && card.getName().toLowerCase().equals(characterGuess.toLowerCase())) {
                return card;
            } else if (card instanceof PlaceCards && card.getName().toLowerCase().equals(placeGuess.toLowerCase()) ){
                return card;
            } else if (card instanceof RoomCards && ((RoomCards) card).getRoomNo() == roomGuess.getRoomNo()) {
                return card;
            }
        }
        return null;
    }
}

public class DiamondMystery {

    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {

        int numPlayers = getNumberOfPlayers();

        List<Cards> characterCards = initializeCharacterCards();
        List<Cards> placeCards = initializePlaceCards();
        List<RoomCards> roomCards = initializeRoomCards();

        Collections.shuffle(characterCards);
        Collections.shuffle(placeCards);
        Collections.shuffle(roomCards);

        Cards hiddenCharacterCard = characterCards.get(0);
        Cards hiddenPlaceCard = placeCards.get(0);
        RoomCards hiddenRoomCard = roomCards.get(0);

        List<Cards> combinedCards = new ArrayList<>();
        combinedCards.addAll(characterCards.subList(1, characterCards.size()));
        combinedCards.addAll(placeCards.subList(1, placeCards.size()));
        combinedCards.addAll(roomCards.subList(1, roomCards.size()));

        Collections.shuffle(combinedCards);

        List<Players> players = new ArrayList<>();
        Players realPlayer = new Players("You");
        players.add(realPlayer);
        for (int i = 1; i < numPlayers; i++) {
            Players player = new Players("Player " + i);
            players.add(player);
        }

        int cardsPerPlayer = combinedCards.size() / numPlayers;
        for (int i = 0; i < numPlayers; i++) {
            Players player = players.get(i);
            for (int j = 0; j < cardsPerPlayer; j++) {
                player.getHand().add(combinedCards.remove(0));
            }
        }

        System.out.println("Cards visible to all:");
        while (!combinedCards.isEmpty()) {
            Cards boardCard = combinedCards.remove(0);
            System.out.println(boardCard.getDetails());
        }

        System.out.println("Your cards are:");
        for (Cards card : realPlayer.getHand()) {
            System.out.println(card.getDetails());
        }

        boolean gameOver = false;
        int currentPlayerIndex = 0;
        boolean firstRound = true;
        while (!gameOver) {
            Players currentPlayer = players.get(currentPlayerIndex);

            if (currentPlayer == realPlayer) {
                gameOver = simulateRealPlayerTurn(realPlayer, hiddenCharacterCard, hiddenPlaceCard, hiddenRoomCard, roomCards, players, firstRound);
            } else {
                simulateRandomPlayerTurn(currentPlayer, roomCards, firstRound);
            }

            currentPlayerIndex = (currentPlayerIndex + 1) % numPlayers;
            firstRound = false;
        }

        System.out.println("\nGame over!");
    }

    private static int getNumberOfPlayers() {
        int numPlayers;
        do {
            System.out.print("Enter the number of players (between 3 and 6): ");
            numPlayers = Integer.parseInt(scanner.nextLine());
        } while (numPlayers < 3 || numPlayers > 6);
        return numPlayers;
    }

    private static List<Cards> initializeCharacterCards() {
        List<Cards> characterCards = new ArrayList<>();
        characterCards.add(new CharacterCards("Emma"));
        characterCards.add(new CharacterCards("Liam"));
        characterCards.add(new CharacterCards("Jack"));
        characterCards.add(new CharacterCards("Sophia"));
        characterCards.add(new CharacterCards("Emily"));
        characterCards.add(new CharacterCards("Ella"));
        return characterCards;
    }

    private static List<Cards> initializePlaceCards() {
        List<Cards> placeCards = new ArrayList<>();
        placeCards.add(new PlaceCards("Under the Flowerpot"));
        placeCards.add(new PlaceCards("Hidden Drawer"));
        placeCards.add(new PlaceCards("Behind the Photo"));
        placeCards.add(new PlaceCards("Inside the Box"));
        placeCards.add(new PlaceCards("Under the Table"));
        placeCards.add(new PlaceCards("Top of the Closet"));
        return placeCards;
    }

    private static List<RoomCards> initializeRoomCards() {
        List<RoomCards> roomCards = new ArrayList<>();
        roomCards.add(new RoomCards("Living Room", 1));
        roomCards.add(new RoomCards("Piano Room", 2));
        roomCards.add(new RoomCards("Greenhouse", 3));
        roomCards.add(new RoomCards("Study Room", 4));
        roomCards.add(new RoomCards("Billiard Room", 5));
        roomCards.add(new RoomCards("Bedroom", 6));
        roomCards.add(new RoomCards("Dining Room", 7));
        roomCards.add(new RoomCards("Library", 8));
        roomCards.add(new RoomCards("Kitchen", 9));
        return roomCards;
    }

    private static boolean simulateRealPlayerTurn(Players realPlayer, Cards hiddenCharacterCard, Cards hiddenPlaceCard, RoomCards hiddenRoomCard, List<RoomCards> roomCards, List<Players> players, boolean firstRound) {
        System.out.println("\nYour turn!");
        if (firstRound) {
            System.out.println("You are outside the home.");
        } else {
            System.out.println("You are in " + realPlayer.getCurrentRoom().getName());
        }

        RoomCards previousRoom = realPlayer.getPreviousRoom();
        RoomCards currentRoom = realPlayer.getCurrentRoom();

        while (true) {
            System.out.print("Type 'roll' to roll the dice: ");
            String command = scanner.nextLine();
            if (!command.equalsIgnoreCase("roll")) {
                System.out.println("Invalid command. Please type 'roll'.");
            } else {
                break;
            }
        }

        int dice1 = rollDice();
        int dice2 = rollDice();
        int diceSum = dice1 + dice2;
        System.out.println("You rolled a " + dice1 + " and a " + dice2 + ". Sum: " + diceSum);

        List<RoomCards> availableRooms = getAvailableRooms(diceSum, roomCards, previousRoom, currentRoom, firstRound);

        if (availableRooms.isEmpty()) {
            System.out.println("You have no available rooms to move to.");
            return false;
        }
        System.out.println("Available rooms to move to:");
        for (RoomCards room : availableRooms) {
            System.out.println(room.getRoomNo() + ". " + room.getName());
        }

        System.out.print("Enter the room number to move into: ");
        int chosenRoomNo = Integer.parseInt(scanner.nextLine());
        RoomCards chosenRoom = getRoomByNo(roomCards, chosenRoomNo);

        if (chosenRoom == null) {
            System.out.println("Invalid room number.");
            return false;
        }

        realPlayer.setCurrentRoom(chosenRoom);
        System.out.println("You moved to " + chosenRoom.getName());
        System.out.print("Enter your character guess: ");
        String characterGuess = scanner.nextLine();
        System.out.print("Enter your place guess: ");
        String placeGuess = scanner.nextLine();
        RoomCards roomGuess = chosenRoom;

        realPlayer.makeGuess(characterGuess, placeGuess, roomGuess);

        for (Players player : players) {
            if (player != realPlayer) {
                Cards revealedCard = player.revealCard(characterGuess, placeGuess, roomGuess);
                if (revealedCard != null) {
                    System.out.println(player.getName() + " has a matching card.");
                    System.out.println("You privately see: " + revealedCard.getDetails());

                    System.out.print("Do you want to make a final guess? (yes/no): ");
                    String finalGuessResponse = scanner.nextLine();
                    if (finalGuessResponse.toLowerCase().equals("yes")) {
                        return makeFinalGuess(hiddenCharacterCard, hiddenPlaceCard, hiddenRoomCard);
                    } else {
                        return false;
                    }
                }
            }
        }

        System.out.print("Do you want to make a final guess? (yes/no): ");
        String finalGuessResponse = scanner.nextLine();
        if (finalGuessResponse.toLowerCase().equals("yes")) {
            return makeFinalGuess(hiddenCharacterCard, hiddenPlaceCard, hiddenRoomCard);
        }

        return false;
    }

    private static boolean makeFinalGuess(Cards hiddenCharacterCard, Cards hiddenPlaceCard, RoomCards hiddenRoomCard) {
        System.out.print("Enter your final character guess: ");
        String finalCharacterGuess = scanner.nextLine();
        System.out.print("Enter your final place guess: ");
        String finalPlaceGuess = scanner.nextLine();
        System.out.print("Enter your final room guess (number): ");
        int finalRoomNo = Integer.parseInt(scanner.nextLine());
        RoomCards finalRoomGuess = getRoomByNo(initializeRoomCards(), finalRoomNo);

        if (hiddenCharacterCard.getName().toLowerCase().equals(finalCharacterGuess.toLowerCase()) &&
                hiddenPlaceCard.getName().toLowerCase().equals(finalPlaceGuess.toLowerCase())&&
                hiddenRoomCard.getRoomNo() == finalRoomNo) {
            System.out.println("Congratulations! You solved the mystery!");
            return true;
        } else {
            System.out.println("Incorrect guess. The correct answer was:");
            System.out.println("Character: " + hiddenCharacterCard.getDetails());
            System.out.println("Place: " + hiddenPlaceCard.getDetails());
            System.out.println("Room: " + hiddenRoomCard.getDetails());
            return true;
        }
    }

    private static void simulateRandomPlayerTurn(Players player, List<RoomCards> roomCards, boolean firstRound) {
        System.out.println("\n" + player.getName() + "'s turn.");
        if (firstRound) {
            System.out.println(player.getName() + " is outside the home.");
        } else if (player.getCurrentRoom() != null) {
            System.out.println(player.getName() + " is in " + player.getCurrentRoom().getName());
        }
        RoomCards previousRoom = player.getPreviousRoom();
        RoomCards currentRoom = player.getCurrentRoom();

        int dice1 = rollDice();
        int dice2 = rollDice();
        int diceSum = dice1 + dice2;
        System.out.println(player.getName() + " rolled a " + dice1 + " and a " + dice2 + ". Sum: " + diceSum);

        List<RoomCards> availableRooms = getAvailableRooms(diceSum, roomCards, previousRoom, currentRoom, firstRound);

        RoomCards chosenRoom = availableRooms.get(new Random().nextInt(availableRooms.size()));
        player.setCurrentRoom(chosenRoom);
        System.out.println(player.getName() + " moved to " + chosenRoom.getName());

        String randomCharacterGuess = ((CharacterCards) initializeCharacterCards().get(new Random().nextInt(initializeCharacterCards().size()))).getName();
        String randomPlaceGuess = ((PlaceCards) initializePlaceCards().get(new Random().nextInt(initializePlaceCards().size()))).getName();
        RoomCards randomRoomGuess = chosenRoom;

        player.makeGuess(randomCharacterGuess, randomPlaceGuess, randomRoomGuess);
    }

    private static int rollDice() {
        return new Random().nextInt(6) + 1;
    }

    private static List<RoomCards> getAvailableRooms(int diceSum, List<RoomCards> roomCards, RoomCards previousRoom, RoomCards currentRoom, boolean firstRound) {
        List<RoomCards> availableRooms = new ArrayList<>();
        for (RoomCards room : roomCards) {
            if ((diceSum % 2 == 0 && room.getRoomNo() % 2 == 0) ||
                    (diceSum % 2 != 0 && room.getRoomNo() % 2 != 0)) {
                if (firstRound || currentRoom == null ||
                        (room != currentRoom && room != previousRoom && Math.abs(room.getRoomNo() - currentRoom.getRoomNo()) != 1) ||
                        room.getRoomNo() == 1) {
                    availableRooms.add(room);
                }
            }
        }
        return availableRooms;
    }

    private static RoomCards getRoomByNo(List<RoomCards> roomCards, int roomNo) {
        for (RoomCards room : roomCards) {
            if (room.getRoomNo() == roomNo) {
                return room;
            }
        }
        return null;
    }
}
