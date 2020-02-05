## Exercise
Complete a timer task scheduler in Java without using built-in timers.

## Solution
The solution executes a runnable task exactly once after the specified time interval has elapsed.
The implemented interface to the timer executor is as follows:

> public interface Timer {  
> /**  
> \* Creates and executes a one-shot action that becomes enabled  
> \* after the given delay.  
> \* @param command the task to execute  
> \* @param delay the time from now to delay execution  
> \* @param unit the time unit of the delay parameter  
> */  
>     void schedule(Runnable command, long delay, TimeUnit unit);  
> }

I took the following constraints into consideration:
* The solution must run in a multi-threaded environment and must be thread-safe.
* The solution must support the scenario where up to a million of timers get scheduled rapidly.
* Using high-level primitives that turn this exercise into a simple wrapper is avoided. In other words, I am allowed to use Java-based frameworks or classes from Core Java, except those that have built-in timers and solve the problem for you.

Please follow my comments in code for the known gaps and deficiencies of implementation.

## Requirements
Java 8, JUnit 4.12.
