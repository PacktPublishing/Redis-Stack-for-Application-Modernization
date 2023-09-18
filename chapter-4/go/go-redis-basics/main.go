package main

import (
	"context"
	"fmt"
	"github.com/redis/go-redis/v9"
)

func main() {
	rdb := redis.NewClient(&redis.Options{
		Addr:     "redis-16887.c1.us-east1-2.gce.cloud.redislabs.com:16887",
		Username: "default",
		Password: "redis-stack-2023",
	})

	ctx := context.Background()
	err := rdb.FlushAll(ctx).Err()
	if err != nil {
		panic(err)
	}

	err = rdb.Set(ctx, "client", "go-redis", 0).Err()
	if err != nil {
		panic(err)
	}

	val, err := rdb.Get(ctx, "client").Result()
	if err != nil {
		panic(err)
	}
	fmt.Printf("The value of key 'client' is: %s\n", val)

	cities := []string{"Roma", "Macerata", "Atlantis"}
	err = rdb.RPush(ctx, "cities", cities).Err()
	if err != nil {
		panic(err)
	}
	err = rdb.LPush(ctx, "queue:tasks", "Task-1").Err()
	if err != nil {
		panic(err)
	}
	err = rdb.LPush(ctx, "queue:tasks", "Task-2").Err()
	if err != nil {
		panic(err)
	}
	task, err := rdb.RPop(ctx, "queue:tasks").Result()
	if err != nil {
		panic(err)
	}
	fmt.Printf("The task retrieved from the list 'queue:tasks' is: %s\n", task)

	resp, err := rdb.SAdd(ctx, "employee:ids", "00000001", "00000002").Result()
	if err != nil {
		panic(err)
	}
	fmt.Printf("Added %d employees\n", resp)
	resp, err = rdb.SAdd(ctx, "employee:ids", "00000001").Result()
	if err != nil {
		panic(err)
	}
	fmt.Printf("Added %d employees\n", resp)
	emps, err := rdb.SMembers(ctx, "employee:ids").Result()
	if err != nil {
		panic(err)
	}
	for index, emp := range emps {
		fmt.Printf("All employees[%d]: %s\n", index, emp)
	}
	emp, err := rdb.SIsMember(ctx, "employee:ids", "00000001").Result()
	if err != nil {
		panic(err)
	}
	fmt.Printf("Employee '00000001' exists %t\n", emp)

	sortedSet := []struct {
		Score  float64
		Member interface{}
	}{
		{22.0, "PlayerOne"},
		{10.0, "PlayerTwo"},
		{78.0, "PlayerThree"},
	}
	fmt.Println("Sorted set:")
	fmt.Println(sortedSet)
	for _, item := range sortedSet {
		zItem := redis.Z{
			Score:  item.Score,
			Member: item.Member,
		}
		err := rdb.ZAdd(ctx, "players", zItem).Err()
		if err != nil {
			panic(err)
		}
	}
	fmt.Println("Sorted set saved to Redis successfully")
	players, err := rdb.ZRangeWithScores(ctx, "players", 0, 50).Result()
	for _, player := range players {
		fmt.Printf("Player %s with score %f\n", player.Member, player.Score)
	}
	score, err := rdb.ZScore(ctx, "players", "PlayerTwo").Result()
	fmt.Printf("Player 'PlayerTwo' has score %f\n", score)

	user := map[string]interface{}{
		"firstname": "Luigi",
		"lastname":  "Fugaro",
		"username":  "foogaro",
	}
	fmt.Printf("User %s:\n", user)
	err = rdb.HMSet(ctx, "user:1", user).Err()
	if err != nil {
		panic(err)
	}
	fmt.Println("Hash saved to Redis successfully")
	resp, err = rdb.HSet(ctx, "user:1", "email", "luigi@foogaro.com").Result()
	if err != nil {
		panic(err)
	}
	fmt.Println("Email field added to the Hash user:1")
	firstname, err := rdb.HGet(ctx, "user:1", "firstname").Result()
	if err != nil {
		panic(err)
	}
	fmt.Printf("User Firstname %s\n", firstname)
	allFields, err := rdb.HGetAll(ctx, "user:1").Result()
	if err != nil {
		panic(err)
	}
	fmt.Printf("User all fileds %s\n", allFields)

}
