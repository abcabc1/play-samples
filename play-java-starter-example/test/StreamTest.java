import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class StreamTest {

    List<Data> dataList;
    List<Data> subDataList;

    @Test
    public void match() {
//        1 parent - 1 sub
//        Map<Object, Object> map = subDataList.stream().collect(Collectors.toMap(v->v.getParent().getId(), v->v, (existing, replacement) -> existing));
//        Duplicate key error
//        Map<Object, Object> map = subDataList.stream().collect(Collectors.toMap(v->v.getParent().getId(), v->v));
//        1 parent - many sub
        Map<Object, List<Data>> map = subDataList.stream().collect(Collectors.groupingBy(v -> v.getParent().getId()));

        dataList.forEach(v -> {
            if (map.get(v.getId()) != null) {
                //        1 parent - many sub
                v.setSubList(map.get(v.getId()));
//        1 parent - 1 sub
//                v.setSingleSub((Data) map.get(v.getId()));
            }
        });
        dataList.forEach(System.out::println);
    }

    @Before
    public void create() throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Data data1 = new Data(1L, "name1", 3, 1, simpleDateFormat.parse("2000-01-01"));
        Data data2 = new Data(2L, "name2", 2, 1, simpleDateFormat.parse("2000-01-02"));
        Data data3 = new Data(3L, "name3", 3, 1, simpleDateFormat.parse("2000-01-01"));
        Data data11 = new Data(11L, "name11", 4, 1, simpleDateFormat.parse("2000-01-04"), new Data(1L));
        Data data12 = new Data(12L, "name12", 5, 1, simpleDateFormat.parse("2000-01-05"), new Data(1L));
        Data data21 = new Data(21L, "name21", 6, 1, simpleDateFormat.parse("2000-01-06"), new Data(2L));

        dataList = Arrays.asList(new Data[]{data1, data2, data3});
        subDataList = Arrays.asList(new Data[]{data11, data12, data21});
    }

    @Test
    public void group() {
//        simple
//        Map<Integer, Map<Date, Long>> map = dataList.stream().collect(Collectors.groupingBy(Data::getAge, Collectors.groupingBy(Data::getDate, Collectors.counting())));
//        complex
//        Map<Integer, Map<Date, IntSummaryStatistics>> map = dataList.stream().collect(Collectors.groupingBy(Data::getAge, Collectors.groupingBy(Data::getDate, Collectors.summarizingInt(Data::getAge))));

        // handle again with entrySet toMap raw key
        Map<Data, List<Data>> map = subDataList.stream().collect(Collectors.groupingBy(v->v.getParent().getId()))
                .entrySet().stream()
                .collect(Collectors.toMap(x -> {
                    int sumAge = x.getValue().stream().mapToInt(Data::getAge).sum();
                    int sumMark= x.getValue().stream().mapToInt(Data::getMark).sum();
                    String concatName = x.getValue().stream().map(Data::getName).distinct().collect(Collectors.joining(","));
                    return new Data(x.getKey(), sumAge, sumMark, concatName);
                }, Map.Entry::getValue));
        // handle again with entrySet toMap reduce key
        /*Map<Data, List<Data>> map = subDataList.stream().collect(Collectors.groupingBy(v->v.getParent().getId()))
                .entrySet().stream()
                .collect(Collectors.toMap(e -> e.getValue().stream().collect(
                        Collectors.reducing((l, r) -> new Data(l.getId(),
                                l.getAge() + r.getAge(),
                                l.getMark() + r.getMark())))
                                .get(),
                        e -> e.getValue()));*/

        // uncompleted
        /*Map<Data, List<Data>> result = subDataList.stream().collect(
                Collectors.collectingAndThen(
                        Collectors.groupingBy(d -> d.getParent().getId()),
                        m -> {
                            m.forEach((k, v) -> v.forEach(k::aggregate));
                            return m;
                        }));*/

        // single summarizing
        /*Map<Long, String> map = subDataList.stream().collect(Collectors.groupingBy(v->v.getParent().getId(),
                        Collectors.collectingAndThen(
                                Collectors.summarizingInt(Data::getAge),
                                dss -> String.format("%7d %3d", dss.getSum(), dss.getCount()))));*/

        // single string concat
//        Map<Long, String> map = subDataList.stream().collect(Collectors.groupingBy(v -> v.getParent().getId(), Collectors.mapping(Data::getName, Collectors.joining(","))));

        subDataList.size();

/*
        TaxLine = title:"New York Tax", rate:0.20, price:20.00
        TaxLine = title:"New York Tax", rate:0.20, price:20.00
        TaxLine = title:"County Tax", rate:0.10, price:10.00
        TaxLine class is

        public class TaxLine {
            private BigDecimal price;
            private BigDecimal rate;
            private String title;
        }

        List<TaxLine> flattened = taxes.stream()
                .collect(Collectors.groupingBy(
                        TaxLine::getTitle,
                        Collectors.groupingBy(
                                TaxLine::getRate,
                                Collectors.reducing(
                                        BigDecimal.ZERO,
                                        TaxLine::getPrice,
                                        BigDecimal::add))))
                .entrySet()
                .stream()
                .flatMap(e1 -> e1.getValue()
                        .entrySet()
                        .stream()
                        .map(e2 -> new TaxLine(e2.getValue(), e2.getKey(), e1.getKey())))
                .collect(Collectors.toList());

        List<TaxLine> lines = Arrays.asList(
                new TaxLine("New York Tax", new BigDecimal("0.20"), new BigDecimal("20.00")),
                new TaxLine("New York Tax", new BigDecimal("0.20"), new BigDecimal("20.00")),
                new TaxLine("County Tax"  , new BigDecimal("0.10"), new BigDecimal("10.00"))
        );
        List<TaxLine> combined =
                lines
                        .stream()
                        .collect(Collectors.groupingBy(TaxGroup::new,
                                Collectors.reducing(BigDecimal.ZERO,
                                        TaxLine::getPrice,
                                        BigDecimal::add)))
                        .entrySet()
                        .stream()
                        .map(TaxGroup::asLine)
                        .collect(Collectors.toList());

        Customer("A",4500,6500)
        Customer("B",3000,3500)
        Customer("C",4000,4500)

        public static Customer merge(Customer first, Customer second) {
            first.setTotal(first.getTotal() + second.getTotal());
            first.setBalance(first.getBalance() + second.getBalance());
            return first;
        }
        Map<String, Customer> retObj =
                listCust.stream()
                        .collect(Collectors.toMap(Customer::getName, Function.identity(), Customer::merge));
        listCust.stream() creates a stream object i.e. Stream<Customer>.
                collect performs a mutable reduction operation on the elements of this stream using the provided Collector.
                The result of toMap is the provided collector, the toMap method extracts the keys Customer::getName and values Function.identity() and if the mapped keys contain duplicates, the merge function Customer::merge is used to resolve collisions.

        if however, your intention is to retrieve a Collection<Customer>:

        Collection<Customer> result = listCust.stream()
                .collect(Collectors.toMap(Customer::getName,
                        Function.identity(),
                        Customer::merge))
                .values();
        or List<Customer> as the result set then all you have to do is call values() and pass the result of that to the ArrayList constructor:

        List<Customer> result = new ArrayList<>(listCust.stream()
                .collect(Collectors.toMap(Customer::getName,
                        Function.identity(),
                        Customer::merge))
                .values());
        Update:

        if you don't want to mutate the objects in the source then simply modify the merge function as follows:

        public static Customer merge(Customer first, Customer second) {
            Customer customer = new Customer(first.getName(), first.getTotal(), first.getBalance());
            customer.setTotal(customer.getTotal() + second.getTotal());
            customer.setBalance(customer.getBalance() + second.getBalance());
            return customer;
        }
        Map<String, Customer> retObj =
                listCust.stream()
                        .collect(Collectors.groupingBy(Customer::getName,
                                Collector.of(
                                        Customer::new,
                                        (c1, c2) -> {
                                            c1.setName(c2.getName());
                                            c1.setTotal(c1.getTotal() + c2.getTotal());
                                            c1.setBalance(c1.getBalance() + c2.getBalance());
                                        },
                                        (c3, c4) -> {
                                            c3.setTotal(c3.getTotal() + c4.getTotal());
                                            c3.setBalance(c3.getBalance() + c4.getBalance());
                                            return c3;
                                        })));

        System.out.println(retObj);
        System.out.println(retObj.values());       //If you want only the list of all Customers
        Output:

        {
            A=Customer [name=A, total=4500.0, balance=6500.0],
            B=Customer [name=B, total=3000.0, balance=3500.0],
            C=Customer [name=C, total=4000.0, balance=4500.0]
        }
        share  improve this answer  follow
        edited Jul 24 '18 at 5:32
        answered Jul 21 '18 at 5:38

                The other answers here are great, but they mutate the Customer instances in the input, which may be unexpected.

                To avoid this, use a custom Collector.

                First, create a method that returns a Collector that takes a Stream<Customer> and merges them into a single Customer:

        public static Collector<Customer, Customer, Customer> customerCollector() {
            return Collector.of(Customer::new, TestBench::merge,
                    (l, r) -> {
                        merge(l, r);
                        return l;
                    });
        }

        public static void merge(final Customer first, final Customer second) {
            first.setName(second.getName());
            first.setTotal(first.getTotal() + second.getTotal());
            first.setBalance(first.getBalance() + second.getBalance());
        }
        This assumes that Customer has a noargs constructor.

        Then you can do:

        Collection<Customer> result = listCust.stream()
                .collect(groupingBy(Customer::getName, customerCollector()))
                .values();

        https://stackoverflow.com/questions/51452737/java-groupingby-sum-multiple-fields
        https://docs.oracle.com/javase/tutorial/collections/streams/reduction.html*/
    }

    @After
    public void shutdown() {

    }

    class Data {
        private Long id;
        private String name;
        private Integer age;
        private Integer mark;
        private Date date;
        private Data parent;
        private List<Data> subList;
        private Data singleSub;
        private Integer sumAge;
        private Integer sumMark;
        private String concatName;

        public Data(Long id, Integer sumAge, Integer sumMark, String concatName) {
            this.id = id;
            this.sumAge = sumAge;
            this.sumMark = sumMark;
            this.concatName = concatName;
        }

        public Data(Long id, Integer sumAge, Integer sumMark) {
            this.id = id;
            this.sumAge = sumAge;
            this.sumMark = sumMark;
        }

        public Data(Long id, String name, Integer age, Integer mark, Date date) {
            this.id = id;
            this.name = name;
            this.age = age;
            this.mark = mark;
            this.date = date;
        }

        public Data(Long id, String name, Integer age, Integer mark, Date date, Data parent) {
            this.id = id;
            this.name = name;
            this.age = age;
            this.mark = mark;
            this.date = date;
            this.parent = parent;
        }

        public Data(Long id) {
            this.id = id;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Integer getAge() {
            return age;
        }

        public void setAge(Integer age) {
            this.age = age;
        }

        public Date getDate() {
            return date;
        }

        public void setDate(Date date) {
            this.date = date;
        }

        public Data getParent() {
            return parent;
        }

        public void setParent(Data parent) {
            this.parent = parent;
        }

        public List<Data> getSubList() {
            return subList;
        }

        public void setSubList(List<Data> subList) {
            this.subList = subList;
        }

        public Data getSingleSub() {
            return singleSub;
        }

        public void setSingleSub(Data singleSub) {
            this.singleSub = singleSub;
        }

        public Integer getMark() {
            return mark;
        }

        public void setMark(Integer mark) {
            this.mark = mark;
        }

        @Override
        public String toString() {
            return "Data{" +
                    "id=" + id +
                    ", name='" + name + '\'' +
                    ", age=" + age +
                    ", mark=" + mark +
                    ", date=" + date +
                    ", parent=" + parent +
                    ", singleSub=" + singleSub +
                    ", subList=" + subList +
                    '}';
        }

        public void aggregate(Data data) {
            this.age += data.age;
            this.mark += data.mark;
            this.name = this.name.concat(",").concat(data.name);
        }
    }
}
