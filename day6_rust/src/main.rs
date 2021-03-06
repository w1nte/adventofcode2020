use std::io::{self, BufRead};
use std::collections::HashMap;


fn add_yes(question : char, map : &mut HashMap<char, i32>) {
    match map.get(&question) {
        Some(n) => {
            let next_n = *n + 1;
            map.insert(question, next_n);
        },
        None => {
            map.insert(question, 1);
        }
    }
}

fn parse_line(line : &String, map : &mut HashMap<char, i32>) {
    for c in line.chars() {
        add_yes(c, map)
    }
}

fn number_of_questions(map : & HashMap<char, i32>, pcount : i32) -> usize {
    let mut q = 0;
    for (_k, _v) in map.iter()
        .filter(|(_k, v)| *v == &pcount) {
        q += 1;
    }
    q
}

fn main() {
    let mut qcount = 0;
    let mut pcount = 0;
    let mut map = HashMap::new();

    let stdin = io::stdin();
    for line in stdin.lock().lines() {
        let input = line.unwrap();

        if input.chars().count() == 0 {
            let num = number_of_questions(&map, pcount);
            qcount += num;
            pcount = 0;
            map = HashMap::new();
            println!("{} +", num)
        } else {
            pcount += 1;
            parse_line(&input, &mut map);
        }
    }

    let num = number_of_questions(&map, pcount);
    println!("{}", qcount + num)
}
