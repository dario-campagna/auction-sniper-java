# Auction Sniper

The worked example presented in Part III of [Growing Object Oriented Software Guided by Tests](http://www.growing-object-oriented-software.com/).

## The Application

Auction Sniper is a Java Swing application that watches online auctions and automatically bids slightly higher whenever 
the price changes, until it reaches a stop-price or the auction closes.
The protocol for bidding in auctions uses XMPP (Jabber) for its underlying communication layer.

To test drive the application we use the XMPP message broker open source implementation [Openfire](https://www.igniterealtime.org/projects/openfire/) 
and its associated client library [Smack](https://www.igniterealtime.org/projects/smack/index.jsp).
We use WindowLicker as high-level test framework to work with Swing and Smack. We use JUnit as our test framework

## Openfire

End-to-end tests need a running Openfire server to let the application talk to a stub auction house.
Instead of having Openfire installed locally on development machine, we use a Vagrant box provisioned with Ansible.
  
To start and provision the vagrant environment run `vagrant up` from the `openfire-vagrant` folder. Navigate to 
[http://localhost:9090](http://localhost:9090) to access Openfire administration console (username **admin**, password _admin_).
  
Openfire is set up with three user accounts and passwords:

- **sniper** _sniper_
- **auction-item-54321** _auction_
- **auction-item-65432** _auction_ 

We kept the default value for "XMPP Domain Name" and the “Server Host Name”, i.e., broker.
We set it up to not store offline messages, which mean there is no persistent state.
We set the resource policy to “Never kick,” which will not allow a new resource to log in if there’s a conflict.