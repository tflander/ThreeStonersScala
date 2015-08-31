Concurrency Challenge:  Three Stoners
===============

Write a program in your favorite language to simulate three stoners smoking
 weed.

Da Rules:

- Each of the three stoners runs in a separate thread.  The three threads must interact as
 three independent tasks or entities.
- One stoner has a supply of weed.  Another has rolling papers, and the third has matches.
- Your choice if the supplies are limited or unlimited (unlimited may be easier).
- You can get 7-12 tokes off a joint before it is necessary to roll a new one.
- Here is the main flow:
  - One of the stoners is designated as the roller.
  - The other two stoners put some of their supply on the table.
  - The stoner designated as the roller takes the supplies off the table and adds his own.
  - The roller rolls a joint, lights it, takes a toke, and passes it.
  - The next stoner takes the joint, takes a toke and passes it.
  - Repeat step e until a stoner takes the last toke of the joint.  This stoner is designated as the new roller.
  - Repeat steps b through f until some terminating event occurs.  This could be based on a timer, a stoner running out of supply, passing out, or some other event.

The goal is to have fun, think creatively, and to learn.  Hopefully we
 can get together in a few weeks and demo different solutions.  Smoking weed in real life is not recommended.
