package com.gobuddy.service;

import com.gobuddy.dto.SearchHistoryResponse;
import com.gobuddy.dto.TransportOptionResponse;
import com.gobuddy.model.Search;
import com.gobuddy.model.User;
import com.gobuddy.repository.SearchRepository;
import com.gobuddy.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SearchService {

    private final SearchRepository searchRepository;
    private final UserRepository userRepository;

    // ─── CITY DISTANCE MAP (approximate km) ──────────────────────────────────
    // Used to calculate dynamic prices and durations
    private static final Map<String, Map<String, Integer>> CITY_DISTANCES = new HashMap<>();

    static {
        // Add distances between major Indian cities (in km)
        addDistance("delhi",       "mumbai",     1400);
        addDistance("delhi",       "bangalore",  2150);
        addDistance("delhi",       "chennai",    2200);
        addDistance("delhi",       "kolkata",    1500);
        addDistance("delhi",       "hyderabad",  1575);
        addDistance("delhi",       "pune",       1450);
        addDistance("delhi",       "ahmedabad",  950);
        addDistance("delhi",       "jaipur",     280);
        addDistance("delhi",       "lucknow",    550);
        addDistance("delhi",       "goa",        1900);
        addDistance("delhi",       "bhopal",     780);
        addDistance("delhi",       "nagpur",     1100);
        addDistance("delhi",       "surat",      1100);
        addDistance("delhi",       "chandigarh", 250);
        addDistance("delhi",       "amritsar",   450);
        addDistance("delhi",       "varanasi",   820);
        addDistance("delhi",       "patna",      1000);
        addDistance("delhi",       "indore",     960);
        addDistance("delhi",       "agra",       210);
        addDistance("delhi",       "shimla",     350);

        addDistance("mumbai",      "bangalore",  980);
        addDistance("mumbai",      "chennai",    1330);
        addDistance("mumbai",      "kolkata",    2050);
        addDistance("mumbai",      "hyderabad",  710);
        addDistance("mumbai",      "pune",       150);
        addDistance("mumbai",      "ahmedabad",  530);
        addDistance("mumbai",      "goa",        600);
        addDistance("mumbai",      "nagpur",     870);
        addDistance("mumbai",      "surat",      290);
        addDistance("mumbai",      "lucknow",    1400);
        addDistance("mumbai",      "jaipur",     1150);
        addDistance("mumbai",      "bhopal",     780);
        addDistance("mumbai",      "indore",     590);
        addDistance("mumbai",      "varanasi",   1500);

        addDistance("bangalore",   "chennai",    350);
        addDistance("bangalore",   "hyderabad",  570);
        addDistance("bangalore",   "kolkata",    1870);
        addDistance("bangalore",   "pune",       840);
        addDistance("bangalore",   "goa",        560);
        addDistance("bangalore",   "cochin",     530);
        addDistance("bangalore",   "mysore",     140);
        addDistance("bangalore",   "coimbatore", 360);

        addDistance("chennai",     "hyderabad",  630);
        addDistance("chennai",     "kolkata",    1700);
        addDistance("chennai",     "cochin",     700);
        addDistance("chennai",     "coimbatore", 500);
        addDistance("chennai",     "madurai",    460);

        addDistance("kolkata",     "hyderabad",  1500);
        addDistance("kolkata",     "patna",      580);
        addDistance("kolkata",     "bhubaneswar",440);
        addDistance("kolkata",     "guwahati",   1000);
        addDistance("kolkata",     "varanasi",   680);

        addDistance("hyderabad",   "pune",       560);
        addDistance("hyderabad",   "goa",        730);
        addDistance("hyderabad",   "nagpur",     500);
        addDistance("hyderabad",   "vijayawada", 275);

        addDistance("lucknow",     "mumbai",     1400);
        addDistance("lucknow",     "bangalore",  2100);
        addDistance("lucknow",     "chennai",    2200);
        addDistance("lucknow",     "kolkata",    1000);
        addDistance("lucknow",     "hyderabad",  1300);
        addDistance("lucknow",     "pune",       1400);
        addDistance("lucknow",     "ahmedabad",  1200);
        addDistance("lucknow",     "jaipur",     600);
        addDistance("lucknow",     "varanasi",   330);
        addDistance("lucknow",     "patna",      450);
        addDistance("lucknow",     "bhopal",     620);
        addDistance("lucknow",     "agra",       370);
        addDistance("lucknow",     "goa",        2000);
        addDistance("lucknow",     "nagpur",     950);

        addDistance("jaipur",      "mumbai",     1150);
        addDistance("jaipur",      "ahmedabad",  660);
        addDistance("jaipur",      "pune",       1200);
        addDistance("jaipur",      "lucknow",    600);
        addDistance("jaipur",      "agra",       240);
        addDistance("jaipur",      "udaipur",    400);

        addDistance("ahmedabad",   "pune",       660);
        addDistance("ahmedabad",   "surat",      265);
        addDistance("ahmedabad",   "indore",     480);
        addDistance("ahmedabad",   "goa",        1050);

        addDistance("pune",        "goa",        460);
        addDistance("pune",        "nagpur",     700);
        addDistance("pune",        "hyderabad",  560);

        addDistance("goa",         "bangalore",  560);
        addDistance("goa",         "hyderabad",  730);
        addDistance("goa",         "mumbai",     600);

        addDistance("varanasi",    "patna",      280);
        addDistance("varanasi",    "kolkata",    680);
        addDistance("varanasi",    "allahabad",  130);
    }

    private static void addDistance(String city1, String city2, int km) {
        CITY_DISTANCES.computeIfAbsent(city1, k -> new HashMap<>()).put(city2, km);
        CITY_DISTANCES.computeIfAbsent(city2, k -> new HashMap<>()).put(city1, km);
    }

    // ─── GET DISTANCE BETWEEN ANY TWO CITIES ────────────────────────────────
    private int getDistance(String from, String to) {
        String f = from.trim().toLowerCase();
        String t = to.trim().toLowerCase();

        // Check direct lookup
        if (CITY_DISTANCES.containsKey(f) && CITY_DISTANCES.get(f).containsKey(t)) {
            return CITY_DISTANCES.get(f).get(t);
        }

        // Check partial match (e.g., "new delhi" -> "delhi")
        for (Map.Entry<String, Map<String, Integer>> entry : CITY_DISTANCES.entrySet()) {
            if (f.contains(entry.getKey()) || entry.getKey().contains(f)) {
                for (Map.Entry<String, Integer> inner : entry.getValue().entrySet()) {
                    if (t.contains(inner.getKey()) || inner.getKey().contains(t)) {
                        return inner.getValue();
                    }
                }
            }
        }

        // Default distance for unknown city pairs (800 km average)
        return 800;
    }

    // ─── CALCULATE DYNAMIC PRICE BASED ON DISTANCE ──────────────────────────
    private BigDecimal busPrice(int km, double multiplier) {
        // Base: ₹0.35 per km for bus
        return BigDecimal.valueOf(Math.round(km * 0.35 * multiplier / 10.0) * 10);
    }

    private BigDecimal trainPrice(int km, double multiplier) {
        // Base: ₹0.75 per km for train
        return BigDecimal.valueOf(Math.round(km * 0.75 * multiplier / 50.0) * 50);
    }

    private BigDecimal flightPrice(int km, double multiplier) {
        // Base: ₹2.5 per km for flight, minimum ₹1500
        long price = Math.round(km * 2.5 * multiplier / 100.0) * 100;
        return BigDecimal.valueOf(Math.max(price, 1500));
    }

    // ─── CALCULATE DURATION BASED ON DISTANCE ───────────────────────────────
    private String busDuration(int km) {
        // Average bus speed: 60 km/h
        int totalMinutes = (int) (km / 60.0 * 60);
        int hours = totalMinutes / 60;
        int minutes = totalMinutes % 60;
        return String.format("%dh %02dm", hours, minutes);
    }

    private String trainDuration(int km) {
        // Average train speed: 80 km/h
        int totalMinutes = (int) (km / 80.0 * 60);
        int hours = totalMinutes / 60;
        int minutes = totalMinutes % 60;
        return String.format("%dh %02dm", hours, minutes);
    }

    private String flightDuration(int km) {
        // Average flight speed: 800 km/h + 45 min airport time
        int totalMinutes = (int) (km / 800.0 * 60) + 45;
        int hours = totalMinutes / 60;
        int minutes = totalMinutes % 60;
        return String.format("%dh %02dm", hours, minutes);
    }

    // ─── CALCULATE ARRIVAL TIME ──────────────────────────────────────────────
    private String calcArrival(String departure, String duration) {
        try {
            String[] depParts = departure.split(":");
            int depHour = Integer.parseInt(depParts[0]);
            int depMin  = Integer.parseInt(depParts[1]);

            String[] durParts = duration.replace("m", "").split("h ");
            int durHours = Integer.parseInt(durParts[0].trim());
            int durMins  = durParts.length > 1
                    ? Integer.parseInt(durParts[1].trim()) : 0;

            int totalMins = depHour * 60 + depMin + durHours * 60 + durMins;
            int arrHour = (totalMins / 60) % 24;
            int arrMin  = totalMins % 60;

            return String.format("%02d:%02d", arrHour, arrMin);
        } catch (Exception e) {
            return "N/A";
        }
    }

    // ─── MAIN SEARCH METHOD ──────────────────────────────────────────────────
    public List<TransportOptionResponse> search(String from, String to,
                                                LocalDate date, String type,
                                                String userEmail) {
        // Save search history for logged-in users
        if (userEmail != null) {
            userRepository.findByEmail(userEmail).ifPresent(user -> {
                Search search = Search.builder()
                        .user(user)
                        .fromLocation(from)
                        .toLocation(to)
                        .travelDate(date)
                        .build();
                searchRepository.save(search);
            });
        }

        int distance = getDistance(from, to);

        List<TransportOptionResponse> results = new ArrayList<>();

        if (type == null || type.equalsIgnoreCase("ALL") || type.equalsIgnoreCase("BUS")) {
            results.addAll(fetchBusOptions(from, to, distance));
        }
        if (type == null || type.equalsIgnoreCase("ALL") || type.equalsIgnoreCase("TRAIN")) {
            results.addAll(fetchTrainOptions(from, to, distance));
        }
        if (type == null || type.equalsIgnoreCase("ALL") || type.equalsIgnoreCase("FLIGHT")) {
            // Only show flights for longer distances (> 300 km)
            if (distance > 300) {
                results.addAll(fetchFlightOptions(from, to, distance));
            }
        }

        // Sort by price
        results.sort(Comparator.comparing(TransportOptionResponse::getPrice));
        return results;
    }

    // ─── BUS OPTIONS ────────────────────────────────────────────────────────
    private List<TransportOptionResponse> fetchBusOptions(String from, String to, int km) {
        String dur1 = busDuration(km);
        String dur2 = busDuration((int)(km * 1.05));
        String dur3 = busDuration((int)(km * 1.10));
        String dur4 = busDuration((int)(km * 0.95));

        return List.of(
                build("BUS", "RedBus Express",
                        busPrice(km, 1.0), dur1, "06:00", calcArrival("06:00", dur1),
                        "https://www.redbus.in", from, to),

                build("BUS", "VRL Travels",
                        busPrice(km, 1.4), dur2, "22:00", calcArrival("22:00", dur2),
                        "https://www.redbus.in", from, to),

                build("BUS", "SRS Travels",
                        busPrice(km, 1.2), dur3, "08:30", calcArrival("08:30", dur3),
                        "https://www.redbus.in", from, to),

                build("BUS", "Orange Travels",
                        busPrice(km, 0.85), dur4, "21:00", calcArrival("21:00", dur4),
                        "https://www.redbus.in", from, to),

                build("BUS", "Neeta Travels",
                        busPrice(km, 1.1), dur1, "23:30", calcArrival("23:30", dur1),
                        "https://www.redbus.in", from, to),

                build("BUS", "IntrCity SmartBus",
                        busPrice(km, 1.6), dur4, "20:00", calcArrival("20:00", dur4),
                        "https://www.redbus.in", from, to)
        );
    }

    // ─── TRAIN OPTIONS ───────────────────────────────────────────────────────
    private List<TransportOptionResponse> fetchTrainOptions(String from, String to, int km) {
        String dur1 = trainDuration(km);
        String dur2 = trainDuration((int)(km * 0.9));
        String dur3 = trainDuration((int)(km * 1.1));
        String dur4 = trainDuration((int)(km * 0.8));

        return List.of(
                build("TRAIN", "Rajdhani Express",
                        trainPrice(km, 1.5), dur2, "16:55", calcArrival("16:55", dur2),
                        "https://www.irctc.co.in", from, to),

                build("TRAIN", "Shatabdi Express",
                        trainPrice(km, 1.3), dur4, "06:00", calcArrival("06:00", dur4),
                        "https://www.irctc.co.in", from, to),

                build("TRAIN", "Vande Bharat Express",
                        trainPrice(km, 1.6), dur4, "05:50", calcArrival("05:50", dur4),
                        "https://www.irctc.co.in", from, to),

                build("TRAIN", "Duronto Express",
                        trainPrice(km, 1.2), dur3, "23:05", calcArrival("23:05", dur3),
                        "https://www.irctc.co.in", from, to),

                build("TRAIN", "Garib Rath Express",
                        trainPrice(km, 0.8), dur1, "15:30", calcArrival("15:30", dur1),
                        "https://www.irctc.co.in", from, to),

                build("TRAIN", "Humsafar Express",
                        trainPrice(km, 1.0), dur1, "20:00", calcArrival("20:00", dur1),
                        "https://www.irctc.co.in", from, to),

                build("TRAIN", "Jan Shatabdi",
                        trainPrice(km, 0.6), dur3, "05:45", calcArrival("05:45", dur3),
                        "https://www.irctc.co.in", from, to)
        );
    }

    // ─── FLIGHT OPTIONS ──────────────────────────────────────────────────────
    private List<TransportOptionResponse> fetchFlightOptions(String from, String to, int km) {
        String dur1 = flightDuration(km);
        String dur2 = flightDuration((int)(km * 1.05));

        return List.of(
                build("FLIGHT", "IndiGo",
                        flightPrice(km, 1.0), dur1, "06:15", calcArrival("06:15", dur1),
                        "https://www.goindigo.in", from, to),

                build("FLIGHT", "Air India",
                        flightPrice(km, 1.3), dur1, "08:30", calcArrival("08:30", dur1),
                        "https://www.airindia.in", from, to),

                build("FLIGHT", "SpiceJet",
                        flightPrice(km, 0.85), dur2, "13:00", calcArrival("13:00", dur2),
                        "https://www.spicejet.com", from, to),

                build("FLIGHT", "Vistara",
                        flightPrice(km, 1.5), dur1, "17:45", calcArrival("17:45", dur1),
                        "https://www.airvistara.com", from, to),

                build("FLIGHT", "Akasa Air",
                        flightPrice(km, 0.9), dur2, "05:30", calcArrival("05:30", dur2),
                        "https://www.akasaair.com", from, to)
        );
    }

    // ─── BUILD HELPER ────────────────────────────────────────────────────────
    private TransportOptionResponse build(String type, String provider,
                                          BigDecimal price, String duration,
                                          String dep, String arr,
                                          String url, String from, String to) {
        return TransportOptionResponse.builder()
                .type(type)
                .providerName(provider)
                .price(price)
                .duration(duration)
                .departureTime(dep)
                .arrivalTime(arr)
                .redirectUrl(url)
                .from(from)
                .to(to)
                .build();
    }

    // ─── SEARCH HISTORY ──────────────────────────────────────────────────────
    public List<SearchHistoryResponse> getHistory(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return searchRepository
                .findByUserUserIdOrderByCreatedAtDesc(user.getUserId())
                .stream()
                .limit(10)
                .map(s -> SearchHistoryResponse.builder()
                        .searchId(s.getSearchId())
                        .fromLocation(s.getFromLocation())
                        .toLocation(s.getToLocation())
                        .travelDate(s.getTravelDate())
                        .createdAt(s.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
    }
}