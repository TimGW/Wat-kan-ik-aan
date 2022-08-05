package nl.watkanikaan.app.data.repository

interface Mapper<Network, Domain> {
    fun mapIncoming(network: Network): Domain
    fun mapOutgoing(domain: Domain): Network
}