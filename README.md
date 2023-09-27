
TEnmo: Online Payment Service API


Overview
TEnmo is an online payment service to facilitate the transfer of "TE bucks" between users. This document provides a brief overview of the core features, database schema, and testing protocols.

Core Features

User Registration & Login:
Users can register with a username and password.
Upon registration, they receive an initial balance of 1,000 TE Bucks.
Users can log in using their credentials and receive an authentication token.

Account Balance:
Authenticated users can view their current TE bucks balance.

Transfers:
Send TE bucks: Users can send specific amounts to others, with multiple conditions for a valid transfer.
View transfers: Allows viewing of all past transfers.
Transfer details: Users can retrieve the specifics of any past transfer via its ID.

Requests:
Users can request TE bucks from others.
They can also view, approve, or reject pending transfer requests.

