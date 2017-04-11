const INDICATOR_NAMES = {
  i002: {
	  name: "Low Bid Price",
    description: "Winning supplier provides a substantially lower bid price than competitors",
    long_desc: `Indicator: Winning supplier provides a substantially lower bid price (25% or more) than competitors.
Eligibility: Award has been made; Procurement method is competitive.
Thresholds: Winning supplier's bid price must be 25% (or more) lower than next competitor.
Description: If the winning supplier's bid is substantially lower than those of its competitors, it could be a sign that the winner had access to information that other bidders did not. It could also signal that the winner is providing a reduced bid in order to win and anticipates a price markup after the award phase is complete. This indicator may be linked to fraud or process rigging.`
  },
  i003: {
	  name: "Only Winner Eligible",
    description: "Only the winning bidder was eligible to have received the contract for a tender when 2+ bidders apply",
    long_desc: `Indicator: Only the winning bidder was eligible to have received the contract for a tender when 2 or more bidders apply.
Eligibility: Award has been made.
Thresholds: 2 or more suppliers must have submitted bids.
Description: When a competitive contracting process receives a single eligible bid among other ineligible bids, there is a possibility that the ineligible bids are fictitious, serving only the purpose of giving the appearance of a competitive process. It could also suggest that the bid has been rigged to favor one bidder or exclude others, including by withholding necessary information or misinforming some bidders. This could be a sign of either fraud or process rigging.`
  },
  i004: {
	  name: "Ineligible Direct Award",
    description: "Sole source award is awarded above the competitive threshold despite legal requirements",
    long_desc: `Indicator: A sole source contract is awarded above the competitive threshold. 
Eligibility: Award has been made; Procurement method is sole source (direct).
Thresholds: Sole sorce thresholds used in accordance with local legislation.
Description: This process rigging flag suggests that competitive bidding rules have been ignored to enable a direct bidding process to go forward when a competitive method was required. `
  },
  i007: {
	  name: "Single Bidder Only",
    description: "This awarded competitive tender only featured a single bid ",
    long_desc: `Indicator: This awarded competitive tender features a single bid only. 
Eligibility: Award has been made; Procurement method is competitive.
Thresholds: Only one supplier has offered a bid.
Description: The presence of a single bidder only in a competitive process could suggest that the bidding process has been altered to favor an individual bidder or to exclude others. `
  },
  i019: {
	  name: "Contract Negotiation Delay",
    description: "Long delays in contract negotiations or award (as bribe demands are possibly negotiated)",
    long_desc: `Indicator: Long delays in contract negotiations or award. 
Eligibility: Award has been made.
Thresholds: Award period is longer than 60 days.
Description: A long delay in the negotiation of a contract or assigning of an award could indicate that malfeasance is taking place in the form or bribery negotiation or other illicit behaviors.`
  },
  i038: {
	  name: "Short Bid Period",
    description: "Bid period is shorter than 7 number of days ",
    long_desc: `Indicator: Bid period is shorter than 3 days.
Eligibility: Bid period complete; Procurement method is competitive.
Thresholds: 3-day bid period.
Description: A particularly short bidding period could indicate that the period for suppliers to submit their bids was reduced to favor certain suppliers over others.`
  },
  i077: {
	  name: "Multiple Contract Winner",
    description: "High number of contract awards to one supplier within a given time period by a single procurement entity",
    long_desc: `Indicator: A high number of contracts are awarded to one supplier within a one-year time period by a single procuring entity.
Eligibility: Award has been made.
Thresholds: Minimum of 2 awards made to 1 supplier by 1 procuring entity.
Description: When a single supplier wins multiple contracts from a procuring entity within a condensced timeframe, it may indicate an effort to favor an individual supplier, which may result in kickbacks to individuals involved in the evaluation or award process. This indicator takes into account competitive, limited and direct bid processes.`
  },
  i083: {
	  name: "Winner-Loser Pattern",
    description: "When X supplier wins, same tenderers always lose (this could be linked to a certain PE)",
    long_desc: `Indicator: When one supplier wins, the same tenderers always lose.
Eligibility: Award has been made; Procurement method is competitive; At least 2 suppliers bid.
Thresholds: 1 supplier wins at least 2 awards against the same group of bidders.
Description: This flag identifies a pattern in which an individual supplier wins agains the same group of bidders more than once. This could suggest the presence of fraud, in that other bidders may be fictitious, or collusion, in that bidders may be submitting non-competitive bids to give the appearance of a competitive process.`
  },
  i085: {
	  name: "Whole % Bid Prices",
    description: "Difference between bid prices is an exact percentage (whole number)",
    long_desc: `Indicator: The difference between bid prices is an exact percentage (a whole number).
Eligibility: Award has been made; Procurement method is competitive; At least 2 bidders submitted bids.
Thresholds: Any two bids are an exact percentage apart (e.g. 2 bids differ by exactly 3%).
Description: It is extremely rare that two bids will be a whole-number percentage apart (eg. exactly 3% apart). The presence of two such bids in a contracting process could signal collusion, in that one bidder cast an invalid bid to buffer the bid proposal of another bidder, or fraud, in that a fake bid was submitted for a similar purpose.`
  },
  i171: {
	  name: "Bid Near Estimate",
    description: "Winning bid is within 1% of estimated price",
    long_desc: `Indicator: Winning bid is within 1% of estimated price.
Eligibility: Award has been made; Procurement method is competitive.
Thresholds: None.
Description: In most instances, the estimated price of a good or work is 7%-12% higher than the actual competitive price. If a contract is awarded within 1% of the estimated price (or even higher than the estimated price) there is a possibility that the supplier was aware of the estimated price (by accessing inside information). The presence of shadow bidding, in which the winning supplier is bidding on behalf of a clandestine entity, is another possible cause.`
  },
  i180: {
	  name: "Multiple Direct Awards",
    description: "Supplier receives multiple single-source/non-competitive contracts from a single procuring entity during a defined time period",
    long_desc: `Indicator: Supplier receives multiple sole-source/non-competitive contracts from a single procuring entity during a defined time period.
Eligibility: Award has been made; Procurement method is sole source (direct).
Thresholds: Supplier must receive at least 2 awards from a single procuring entity; Time period is 1 calendar year.
Description: This flag identifies instances in which a single supplier receives multiple direct contracts during a one-year period. This could be an indication of split bidding, where a larger award is split into two or more smaller awards to avoid breaching the competitive threshold, or of a preference for an individual supplier, which may provide kickbacks to individuals involved in the award process.`
  }
};

export default INDICATOR_NAMES;
